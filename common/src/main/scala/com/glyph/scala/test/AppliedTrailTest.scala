package com.glyph.scala.test

import com.badlogic.gdx.graphics.{GL10, Color, Texture, Mesh}
import com.glyph.scala.lib.util.{Logging, Timing}
import com.glyph.scala.lib.libgdx.screen.ConfiguredScreen
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.glyph.scala.lib.libgdx.gl.{BaseTrail, BaseStripBatch, ShaderHandler}
import com.badlogic.gdx.Gdx
import aurelienribon.tweenengine._
import com.badlogic.gdx.graphics.g2d.{Sprite, TextureRegion}
import com.glyph.scala.lib.libgdx.actor.SpriteBatchRenderer
import com.badlogic.gdx.scenes.scene2d.Group
import com.glyph.scala.game.Glyphs
import Glyphs._
import com.badlogic.gdx.math.MathUtils
import com.glyph.scala.lib.util.pool.Pool

abstract class TrailTestEnvironment(shader: ShaderHandler, batch: BaseStripBatch) extends ConfiguredScreen with Logging with Timing {
  autoClearScreen = false
  Mesh.forceVBO = true
  val renderer = new Group with SpriteBatchRenderer
  root.addActor(renderer)
  val SRC_FUNC: Int = GL10.GL_SRC_ALPHA
  val DST_FUNC: Int = GL10.GL_ONE_MINUS_SRC_ALPHA
  var time = 0f
  ShaderProgram.pedantic = false

  def onShader(s: ShaderProgram){}

  def onBatch(s: ShaderProgram, batch: BaseStripBatch){}

  val trailRenderer = shader.applier {
    s =>
      Gdx.gl.glEnable(GL10.GL_TEXTURE_2D)
      Gdx.gl.glEnable(GL10.GL_BLEND)
      Gdx.gl.glBlendFunc(SRC_FUNC, DST_FUNC)
      s.begin()
      s.setUniformMatrix("u_projTrans", stage.getCamera.combined)
      //s.setUniformi("u_texture", 0)
      s.setUniformf("time", time)
      s.setUniformf("resolution", 1080, 1920)
      s.setUniformf("mouse", 0, 0)
      onShader(s)
      batch.begin()
      onBatch(s, batch)
      batch.end(s)
      s.end()
  }

  override def render(delta: Float) {
    trailRenderer()
    time += delta
    super.render(delta)
  }
}

/**
 * @author glyph
 */
class AppliedTrailTest(nSprites: Int, batch: BaseStripBatch, shader: ShaderHandler, generator: () => BaseTrail, withShader: ShaderProgram => Unit = s => {}, checkTime: Boolean = true)
  extends TrailTestEnvironment(shader, batch) {
  val particleTexture = new Texture(Gdx.files.internal("data/particle.png"))
  implicit val tweenManager = new TweenManager()
  implicit val spritePool = Pool[Sprite](1000)
  val trails = TrailOps.createManualParticles(generator)(particleTexture)(nSprites)
  val sprites = trails map {
    case (sp, _) => sp
  }

  import MathUtils._

  val wRand = () => random(0f, STAGE_WIDTH)
  val hRand = () => random(0f, STAGE_HEIGHT)
  val whRand = () => (wRand(), hRand())
  TrailOps.createAnimation(trails)(whRand, whRand).repeat(-1, 0).start(tweenManager)
  implicit val cls = classOf[Seq[Sprite]]
  renderer.addDrawable(sprites)


  override def onShader(s: ShaderProgram): Unit ={
    super.onShader(s)
    withShader(s)
  }

  override def onBatch(s: ShaderProgram, batch: BaseStripBatch): Unit = {
    super.onBatch(s,batch)
    if (checkTime) {
      printTime("render trails") {
        trails foreach {
          case (sp, trail) =>
            batch.draw(s, trail.meshVertices, trail.count)
          //trail.mesh.render(s, GL10.GL_TRIANGLE_STRIP)
        }
      }
    } else {
      trails foreach {
        case (sp, trail) =>
          batch.draw(s, trail.meshVertices, trail.count)
        //trail.mesh.render(s, GL10.GL_TRIANGLE_STRIP)
      }
    }
  }

  override def render(delta: Float): Unit = {
    clearScreen()
    tweenManager.update(delta)
    if (checkTime) {
      printTime("updateMesh") {
        trails foreach {
          case (sp, trail) => trail.add(sp.getX + sp.getWidth / 2, sp.getY + sp.getHeight / 2)
        }
      }
    } else {
      trails foreach {
        case (sp, trail) => trail.add(sp.getX + sp.getWidth / 2, sp.getY + sp.getHeight / 2)
      }
    }
    super.render(delta)
  }
}

object TrailOps {

  import Glyphs._

  def createManualParticles(f: () => BaseTrail)(texture: Texture)(n: Int)(implicit pool:com.glyph.scala.lib.util.pool.Pool[Sprite]): Seq[(Sprite, BaseTrail)] = 1 to n map {
    _ =>
      val sp = manual[Sprite]
      sp.setTexture(texture)
      sp.asInstanceOf[TextureRegion].setRegion(0, 0, texture.getWidth, texture.getHeight)
      sp.setSize(10, 10)
      sp.setColor(Color.WHITE)
      sp -> f()
  }

  def createAnimation(trails: Seq[(Sprite, BaseTrail)])(from: () => (Float, Float), to: () => (Float, Float)): Timeline = {
    val timeLine = Timeline.createParallel()
    import SpriteAccessor._
    trails foreach {
      case (sp, trail) =>
        val (x, y) = from()
        val (tx, ty) = to()
        timeLine.push(Tween.set(sp, XY).target(x, y))
        timeLine.push(Tween.
          to(sp, XY, 1).
          target(tx, ty).
          ease(TweenEquations.easeOutExpo).setCallback(new TweenCallback {
          def onEvent(`type`: Int, source: BaseTween[_]): Unit = {
            trail.reset()
          }
        }).setCallbackTriggers(TweenCallback.COMPLETE))
    }
    timeLine
  }

}