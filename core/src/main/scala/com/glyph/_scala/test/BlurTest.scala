package com.glyph._scala.test

import com.glyph._scala.lib.libgdx.screen.ConfiguredScreen
import com.glyph._scala.game.action_puzzle.view.ActionPuzzleTable
import com.glyph._scala.game.action_puzzle.ComboPuzzle
import com.glyph._scala.game.builders.Builders
import com.badlogic.gdx.assets.AssetManager
import com.glyph._scala.lib.libgdx.actor.{SpriteActor, Scissor}
import com.badlogic.gdx.graphics.glutils._
import com.badlogic.gdx.graphics._
import com.badlogic.gdx.scenes.scene2d.{Touchable, Actor}
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.{MathUtils, Rectangle, Matrix4}
import com.badlogic.gdx.Gdx
import com.glyph._scala.lib.util.Disposable
import com.glyph._scala.lib.libgdx.gl.ShaderHandler
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.glyph._scala.lib.libgdx.actor.widgets.Layered
import com.glyph._scala.lib.libgdx.actor.blend.AdditiveBlend
import scala.util.Try
import com.glyph._scala.lib.util.updatable.task.ParallelProcessor

/**
 * @author glyph
 */
class BlurTest extends ConfiguredScreen {
  import Builders._
  implicit val assetManager = new AssetManager
  var blurStep = 5f
  val PINGPONG_STEP = 0
  val puzzle = new ComboPuzzle
  val root2 = new Layered {}
  val table = new ActionPuzzleTable(puzzle)(
    roundRectTexture.forceCreate,
    particleTexture.forceCreate,
    dummyTexture.forceCreate,
    flat.forceCreate
  ) with FrameCapture {
    override def bufferWidth: Int = 256

    override def bufferHeight: Int = 256

    override def shouldRenderer: Boolean = true
  }
  val sword = swordTexture.forceCreate
  ShaderProgram.pedantic = true

  //the blurring of this resolution ends within 50ms, and this can be done at back ground.
  val pingpong = Array(0, 0).map(_ => new FrameBuffer(Pixmap.Format.RGB565, 256, 256, false))
  val a_position = new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE)
  val a_texCoord = VertexAttribute.TexCoords(0)
  val rect = new Mesh(true, 4, 0, a_position, a_texCoord)
  rect.setVertices(rectangle(-0.5f, -0.5f, 1f, 1f))
  val shader = ShaderHandler("shader/blur.vert", "shader/blur.frag").applier2 {
    s =>
      val camera = new OrthographicCamera(1, 1)
      () => {
        camera.update()
        table.buffer.getColorBufferTexture.bind(0)
        //sword.bind(0)
        s.begin()
        s.setUniformMatrix("u_projModelView", camera.combined)
        s.setUniformi("u_sampler0", 0)
        s.setUniformf("u_delta", 1f / pingpong(0).getWidth)
        s.setUniformf("u_step",blurStep.toInt.toFloat)
        var i = 0
        while ( i < PINGPONG_STEP){
          s.setUniformi("u_horizontal", 1)
          pingpong(0).begin()
          Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT)
          rect.render(s, GL10.GL_TRIANGLE_STRIP)
          pingpong(0).end()
          pingpong(0).getColorBufferTexture.bind(0)

          s.setUniformi("u_horizontal", 0)
          pingpong(1).begin()
          Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT)
          rect.render(s, GL10.GL_TRIANGLE_STRIP)
          pingpong(1).end()
          pingpong(1).getColorBufferTexture.bind(0)
          i += 1
        }

        s.end()
      }
  }
  val layers = new Layered with AdditiveBlend
  val pingpongActors = pingpong map (buf => new SpriteActor(buf.getColorBufferTexture))
  layers.addActor(table)
  layers.addActor(pingpongActors(1))
  root.add(layers).fill().expand()

  pingpongActors(1).setTouchable(Touchable.disabled)

  var minus = 1f
  override def render(delta: Float): Unit = {
    super.render(delta)
    if (3 <= blurStep && blurStep <= 10){
      blurStep += minus * delta * 1f * 10
    }else{
      minus = -minus
      blurStep = MathUtils.clamp(blurStep,3,10)
    }
    shader()
  }

  def rectangle(x: Float, y: Float, w: Float, h: Float): Array[Float] = Array[Float](
    x, y, 0, 0,
    x, y + h, 0, 1,
    x + w, y, 1, 0,
    x + w, y + h, 1, 1
  )
}

import com.glyph._scala.game.Glyphs._

object BlurUtil {
  def createBlurredTexture(target: Texture)(width: Int, height: Int): Try[FrameBuffer] = Try {
    val shader = new ShaderProgram("shader/blur.vert".internal, "shader/blur.frag".internal)
    if (!shader.isCompiled) {
      throw new RuntimeException("shader compilation failed!\n" + shader.getLog)
    }
    val pingpong = Array(0, 0).map(_ => new FrameBuffer(Pixmap.Format.RGB565, width, height, false))
    val a_position = new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE)
    val a_texCoord = VertexAttribute.TexCoords(0)
    val rect = new Mesh(true, 4, 0, a_position, a_texCoord)
    rect.setVertices(rectangle(-0.5f, -0.5f, 1f, 1f))
    val camera = new OrthographicCamera(1, 1)
    camera.update()
    target.bind(0)
    val s = shader
    s.begin()
    s.setUniformMatrix("u_projModelView", camera.combined)
    s.setUniformi("u_sampler0", 0)
    s.setUniformf("u_delta", 1f / pingpong(0).getWidth)

    s.setUniformi("u_horizontal", 1)
    pingpong(0).begin()
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT)
    rect.render(s, GL10.GL_TRIANGLE_STRIP)
    pingpong(0).end()
    pingpong(0).getColorBufferTexture.bind(0)

    s.setUniformi("u_horizontal", 0)
    pingpong(1).begin()
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT)
    rect.render(s, GL10.GL_TRIANGLE_STRIP)
    pingpong(1).end()
    shader.dispose()
    pingpong(0).dispose()
    rect.dispose()
    pingpong(1)
  }

  def rectangle(x: Float, y: Float, w: Float, h: Float): Array[Float] = Array[Float](
    x, y, 0, 0,
    x, y + h, 0, 1,
    x + w, y, 1, 0,
    x + w, y + h, 1, 1
  )
}

trait FrameCapture extends Actor with Disposable {
  def bufferWidth: Int

  def bufferHeight: Int

  def shouldRenderer: Boolean

  val buffer = new FrameBuffer(Pixmap.Format.RGBA8888, bufferWidth, bufferHeight, false)
  val camera = new OrthographicCamera()
  //this should be the same size as the actor's size.
  val scissorInfo = (camera, new Rectangle(0, 0, bufferWidth, bufferHeight))
  //the view port must be the same size as the framebuffer's size.
  val projection = new Matrix4
  val transform = new Matrix4()
  val idt = new Matrix4()

  override def sizeChanged(): Unit = {
    super.sizeChanged()
    camera.setToOrtho(true, getWidth, getHeight) //this must be updated whenever the size changes.
  }

  override def draw(batch: Batch, parentAlpha: Float): Unit = {
    batch.flush()
    projection.set(batch.getProjectionMatrix)
    transform.set(batch.getTransformMatrix)
    camera.update()
    batch.setTransformMatrix(idt)
    batch.setProjectionMatrix(camera.combined)
    Scissor.push(scissorInfo)
    buffer.begin()
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT)
    super.draw(batch, parentAlpha)
    batch.flush()
    buffer.end()
    batch.setTransformMatrix(transform)
    batch.setProjectionMatrix(projection)
    Scissor.pop()
    if (shouldRenderer) {
      super.draw(batch, parentAlpha)
      //batch.draw(buffer.getColorBufferTexture,getX,getY,getWidth,getHeight)
    }
  }

  override def dispose(): Unit = {
    buffer.dispose()
  }
}
