package com.glyph._scala.game.action_puzzle

import com.glyph._scala.lib.libgdx.gl.{UVTrail, BaseStripBatch, ShaderHandler, BaseTrail}
import com.badlogic.gdx.graphics.{GL20, Color, Texture}
import com.badlogic.gdx.scenes.scene2d.Group
import com.glyph._scala.lib.libgdx.actor.{SBDrawableObject, Tasking, SpriteBatchRenderer}
import com.glyph._scala.lib.libgdx.actor.blend.AdditiveBlend
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.g2d.{Batch, Sprite}
import com.glyph._scala.lib.util.pool.Pool
import scala.collection.mutable.ArrayBuffer
import com.glyph._scala.lib.util.updatable.task.{InterpolatedFunctionTask, TimedFunctionTask}
import com.badlogic.gdx.math.{MathUtils, Interpolation, Matrix4}
import com.badlogic.gdx.Gdx
import com.glyph._scala.lib.util.animator.Explosion
import com.glyph._scala.lib.util.ColorUtil
import com.glyph._scala.game.Glyphs
import Glyphs._
import com.glyph._scala.lib.libgdx.GLFuture
import com.glyph._scala.lib.injection.GLExecutionContext


/**
 * @author glyph
 */
class ParticleRenderer[GivenTrail <: UVTrail : Class](particleTexture: Texture)(shader: ShaderHandler, batch: BaseStripBatch)
  extends Group
  with Tasking
  with SpriteBatchRenderer
  with SBDrawableObject
  with AdditiveBlend {

  import MathUtils._

  ShaderProgram.pedantic = false
  val spriteTrailArray = new com.badlogic.gdx.utils.Array[Seq[(Sprite, GivenTrail)]](1000)
  implicit val velBufPool = Pool[ArrayBuffer[Float]](() => ArrayBuffer[Float]())((buf: ArrayBuffer[Float]) => buf.clear())(1000)
  implicit val trailPool = Pool[GivenTrail](10000)
  implicit val tftPool = Pool[TimedFunctionTask](1000)
  implicit val iftPool = Pool[InterpolatedFunctionTask](1000)
  implicit val spritePool = Pool[Sprite](10000)
  implicit val bufPool = Pool[ArrayBuffer[Sprite]](() => ArrayBuffer[Sprite]())((buf: ArrayBuffer[Sprite]) => buf.clear())(1000)
  velBufPool.preAlloc(6 * 6)
  trailPool.preAlloc(6 * 6 * 20)
  tftPool.preAlloc(6 * 6)
  iftPool.preAlloc(6 * 6)
  spritePool.preAlloc(6 * 6 * 20)
  bufPool.preAlloc(6 * 6)

  //preAlloc[GivenTrail](1000)
  //preAlloc[Sprite](1000)
  val combined = new Matrix4
  // TODO these renderer should be made out of this class

  val tupleRenderer = (s: ShaderProgram) => (tuple: (Sprite, GivenTrail)) => {
    val sp = tuple._1
    val trail = tuple._2
    trail.addTrail(sp.getX + sp.getWidth / 2, sp.getY + sp.getHeight / 2)
    batch.draw(s, trail.meshVertices, trail.count)
  }
  val trailRenderer = shader.applier2 {
    s => {
      val seqRenderer = tupleRenderer(s)
      () => {
        Gdx.gl.glEnable(GL20.GL_TEXTURE_2D)
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(SRC_FUNC, DST_FUNC)
        s.begin()
        s.setUniformMatrix("u_projTrans", combined.set(getStage.getSpriteBatch.getProjectionMatrix).mul(computeTransform()))
        s.setUniformi("u_texture", 0)
        particleTexture.bind()
        batch.begin()
        val itr = spriteTrailArray.iterator()
        while (itr.hasNext) {
          itr.next() foreach seqRenderer
        }
        batch.end(s)
        s.end()
      }
    }
  }

  override def draw(batch: Batch, parentAlpha: Float): Unit = {
    batch.end()
    trailRenderer()
    batch.begin()
    super.draw(batch, parentAlpha)
  }

  def addParticles(x: Float, y: Float, givenColor: Color,number:Int) {
    implicit val bufCls = classOf[ArrayBuffer[Sprite]]
    val duration = 1f
    val buf = manual[ArrayBuffer[Sprite]]
    val velBuf = manual[ArrayBuffer[Float]]
    val ft = auto[TimedFunctionTask]
    val it = auto[InterpolatedFunctionTask]
    //make this particle specific code into trait's code
    var setBuf: Seq[(Sprite, GivenTrail)] = null
    addTask(ft.setFunctions(
      () => {
        //TextureUtil.split(token.sprite)(8)(8)(buf)
        val texture = particleTexture
        1 to number foreach {
          _ => val p = manual[Sprite]
            p.setTexture(texture)
            p.setRegion(0f, 0f, 1f, 1f)
            p.setOrigin(0f, 0f)
            val s = random(3, 30) * 2
            p.setSize(s, s)
            p.setPosition(x, y)
            p.setColor(givenColor)
            buf += p
        }
        Explosion.init(() => random(PI2), () => random(65, 4000), velBuf, buf.length)
        addDrawable(buf)
        setBuf = buf map {
          sp =>
            val trail = manual[GivenTrail]
            trail.color.set(givenColor)
            sp -> trail
        }
        spriteTrailArray.add(setBuf)
      },
      Explosion.update(0, -500, 5f)(buf, velBuf),
      () => {
        removeDrawable(buf)
        buf foreach (_.free)
        buf.free
        setBuf foreach {
          case (sp, trail) => trail.free
        }
        spriteTrailArray.removeValue(setBuf, true)
        velBuf.free
      }) in duration)
    val color = givenColor.cpy()
    val hsv = ColorUtil.ColorToHSV(color)
    hsv.v = 1f
    hsv.s = 0.7f
    color.set(hsv.toColor)
    addTask(it setUpdater (alpha => {
      val a = Interpolation.exp10Out.apply(0.8f, 0, alpha)
      color.a = a
      buf.foreach {
        sp => sp.setColor(color)
      }
    }) in duration * 2)
  }
}

object ParticleRenderer {
  implicit val context = GLExecutionContext
  def futureShader = GLFuture(ShaderHandler("shader/rotate2.vert", "shader/default.frag"))

  def futureBatch = GLFuture(new BaseStripBatch(1000 * 10 * 2, UVTrail.ATTRIBUTES))

  def futureRenderer[GivenTrail <: UVTrail : Class](texture: Texture) = for {
    shader <- futureShader
    batch <- futureBatch
  } yield new ParticleRenderer[GivenTrail](texture)(shader, batch)
}