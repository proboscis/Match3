package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.screen.{ConfiguredScreen, ScreenBuilder}
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.{Gdx, Screen}
import com.glyph.scala.lib.libgdx.gl.ShaderHandler
import com.badlogic.gdx.graphics.glutils.{ShaderProgram, ImmediateModeRenderer, FrameBuffer, ImmediateModeRenderer20}
import com.badlogic.gdx.graphics.Pixmap.Format
import com.badlogic.gdx.graphics._
import com.glyph.scala.game.Glyphs
import Glyphs._
import scalaz._
import Scalaz._
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.glyph.scala.lib.libgdx.actor.SpriteActor

/**
 * @author glyph
 */
class TransformFeedback extends ScreenBuilder {
  def requirements: Set[(Class[_], Seq[String])] = Set(
    classOf[Texture] -> Seq("data/sword.png", "data/dummy.png","data/particle.png")
  )

  def create(implicit assetManager: AssetManager): Screen = new ConfiguredScreen {
    ShaderProgram.pedantic = true
    override def STAGE_WIDTH: Int = 960
    override def STAGE_HEIGHT: Int = 540
    val pointShaderHandler = new ShaderHandler("shader/point.vert", "shader/point.frag")
    val feedbackShaderHandler = new ShaderHandler("shader/feedback.vert", "shader/feedback.frag")
    val pointRenderer = new ImmediateModeRenderer20(1000, false, true, 0)
    val textureRenderer = new ImmediateModeRenderer20(1000, false, true, 1)
    val frameBuffers = Array(1 to 2 map (_ => new FrameBuffer(Format.RGBA8888, 100, 100, false)): _*)

    val camera = new OrthographicCamera(100, 100)
    camera.update()
    val texture: Texture = "data/sword.png".fromAssets
    val dummyTexture: Texture = "data/dummy.png".fromAssets
    val particleTexture:Texture = "data/particle.png".fromAssets
    val SRC_FUNC: Int = GL10.GL_SRC_ALPHA
    val DST_FUNC: Int = GL10.GL_ONE

    val frameBufferActors= frameBuffers map (_.getColorBufferTexture |> SpriteActor.apply)

    val leftTable = new Table
    leftTable.debug()
    val rightTable = new Table
    rightTable.debug()
    frameBufferActors foreach{
      a => rightTable.add(a).fill.expand.row
    }
    root.add(leftTable).fill.expand
    root.add(rightTable).fill.expand

    val default = ImmediateModeRenderer20.createDefaultShader(false,true,0)

    val renderFunction = pointShaderHandler.shader ~ feedbackShaderHandler.shader map {
      case pointShaderOpt ~ feedbackShaderOpt => {
        var failed = false
        () => {
          if (!failed && pointShaderOpt.isDefined && feedbackShaderOpt.isDefined) {
            try {
              val pointShader = pointShaderOpt.get
              val feedbackShader = feedbackShaderOpt.get
              val pr = pointRenderer
              val tr = textureRenderer
              pr.setShader(pointShader)
              //pr.setShader(default)
              tr.setShader(feedbackShader)
              camera.update()
              //shader.begin()
              //shader.setUniformMatrix("u_projTrans", stage.getCamera.combined)
              //shader.setUniformi("u_texture", 0)
              Gdx.gl20.glEnable(GL11.GL_POINT_SPRITE_OES)//this is required!!!
              //// http://stackoverflow.com/questions/13213227/gl-pointcoord-has-incorrect-uninitialized-value
              Gdx.gl.glEnable(GL20.GL_VERTEX_PROGRAM_POINT_SIZE)
              Gdx.gl.glEnable(GL10.GL_BLEND)
              Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
              frameBuffers(0).begin()
              tr.begin(camera.combined, GL10.GL_TRIANGLE_STRIP)
              texture.bind()
              texturedRect(tr)(Color.WHITE, -40, -40, 80, 80)
              tr.end()
              frameBuffers(0).end()
              Gdx.gl.glEnable(GL10.GL_TEXTURE_2D)
              pr.begin(camera.combined, GL10.GL_POINTS)
              frameBuffers(0).getColorBufferTexture.bind()
              pr.color(1, 0, 0, 1)
              pr.vertex(0, 0, 0)
              pr.end()

              //shader.end()
            } catch {
              case e: Throwable => e.printStackTrace(); failed = true
            }
          }
        }
      }
    }

    override def render(delta: Float): Unit = {
      super.render(delta)
      renderFunction()()
    }

    def drawRect(r: ImmediateModeRenderer)(color: Color, x: Float, y: Float, w: Float, h: Float) {
      r.color(color.r, color.g, color.b, color.a)
      r.vertex(x, y, 0)
      r.color(color.r, color.g, color.b, color.a)
      r.vertex(x, y + h, 0)
      r.color(color.r, color.g, color.b, color.a)
      r.vertex(x + w, y, 0)
      r.color(color.r, color.g, color.b, color.a)
      r.vertex(x + w, y + h, 0)
    }

    def texturedRect(r: ImmediateModeRenderer)(color: Color, x: Float, y: Float, w: Float, h: Float) {
      r.color(color.r, color.g, color.b, color.a)
      r.texCoord(0, 0)
      r.vertex(x, y, 0)
      r.color(color.r, color.g, color.b, color.a)
      r.texCoord(0, 1)
      r.vertex(x, y + h, 0)
      r.color(color.r, color.g, color.b, color.a)
      r.texCoord(1, 0)
      r.vertex(x + w, y, 0)
      r.color(color.r, color.g, color.b, color.a)
      r.texCoord(1, 1)
      r.vertex(x + w, y + h, 0)
    }

    def drawPrim1(r: ImmediateModeRenderer) {
      val color = Color.WHITE
      val x = 5
      val y = 5
      val width = 40
      val height = 40
      r.color(color.r, color.g, color.b, color.a)
      r.vertex(x, y, 0)
      r.color(color.r, color.g, color.b, color.a)
      r.vertex(x, y + height, 0)
      r.color(color.r, color.g, color.b, color.a)
      r.vertex(x + width, y, 0)
      r.color(color.r, color.g, color.b, color.a)
      r.vertex(x + width, y + height, 0)
    }
  }
}
