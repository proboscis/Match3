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
import com.glyph.scala.lib.util.Logging
import com.badlogic.gdx.graphics.VertexAttributes.Usage

/**
 * @author glyph
 */
class TransformFeedback extends ScreenBuilder with Logging{
  def requirements: Set[(Class[_], Seq[String])] = Set(
    classOf[Texture] -> Seq("data/sword.png", "data/dummy.png","data/particle.png")
  )

  def create(implicit assetManager: AssetManager): Screen = new ConfiguredScreen {
    ShaderProgram.pedantic = false
    override def STAGE_WIDTH: Int = 960
    override def STAGE_HEIGHT: Int = 540
    val pointShaderHandler = new ShaderHandler("shader/point.vert", "shader/point.frag")
    val feedbackShaderHandler = new ShaderHandler("shader/feedback.vert", "shader/feedback.frag")
    val pointRenderer = new ImmediateModeRenderer20(1000, false, true, 0)
    val textureRenderer = new ImmediateModeRenderer20(1000, false, true, 1)
    val frameBuffers = Array(1 to 2 map (_ => new FrameBuffer(Format.RGBA8888, 16, 16, false)): _*)
    val a_position = new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE)
    val a_texCoord = VertexAttribute.TexCoords(0)
    val attributes = new VertexAttributes(a_position,a_texCoord)
    val rect = new Mesh(true,4,0,attributes)

    {
      val (x,y,w,h)=(-0.5f,-0.5f,1f,1f)
      rect.setVertices(Array[Float](
        x ,y,0,0,
        x ,y+h,0,1,
        x+w ,y,1,0,
        x+w ,y+h,1,1
      ))
    }

    val camera = new OrthographicCamera(1, 1)
    camera.update()

    val frameBufferActors= frameBuffers map (_.getColorBufferTexture |> SpriteActor.apply)

    val leftTable = new Table
    leftTable.debug()
    val rightTable = new Table
    rightTable.debug()
    val texture: Texture = "data/sword.png".fromAssets
    val dummyTexture: Texture = "data/dummy.png".fromAssets
    val particleTexture:Texture = "data/particle.png".fromAssets
    val SRC_FUNC: Int = GL10.GL_SRC_ALPHA
    val DST_FUNC: Int = GL10.GL_ONE
    frameBufferActors foreach{
      a => rightTable.add(a).fill.expand.row
    }
    root.add(leftTable).fill.expand
    root.add(rightTable).fill.expand

    val default = ImmediateModeRenderer20.createDefaultShader(false,true,0)

    var renderFailed = false
    val renderFunction = pointShaderHandler.shader ~ feedbackShaderHandler.shader map {
      case pointShaderOpt ~ feedbackShaderOpt => {
        renderFailed = false
        () => {
          if (!renderFailed && pointShaderOpt.isDefined && feedbackShaderOpt.isDefined) {
            try {
              Gdx.gl20.glEnable(GL11.GL_POINT_SPRITE_OES)//this is required!!!
              //// http://stackoverflow.com/questions/13213227/gl-pointcoord-has-incorrect-uninitialized-value
              Gdx.gl.glEnable(GL20.GL_VERTEX_PROGRAM_POINT_SIZE)
              //log("drawing")
              val ps = pointShaderOpt.get
              val fs = feedbackShaderOpt.get
              val pr = pointRenderer
              val tr = textureRenderer
              pr.setShader(ps)
              tr.setShader(fs)
              camera.update()
              fs.begin()
              texture.bind(0)
              fs.setUniformMatrix("u_projModelView",camera.combined)
              fs.setUniformi("u_sampler0",0)
              fs.setUniformi("u_state",0)
              frameBuffers(0).begin()
              rect.render(fs,GL10.GL_TRIANGLE_STRIP)
              frameBuffers(0).end()
              fs.setUniformi("u_state",1)
              frameBuffers(1).begin()
              rect.render(fs,GL10.GL_TRIANGLE_STRIP)
              frameBuffers(1).end()
              fs.end()


              //shader.begin()
              //shader.setUniformMatrix("u_projTrans", stage.getCamera.combined)
              //shader.setUniformi("u_texture", 0)
              /*
              Gdx.gl.glEnable(GL10.GL_BLEND)
              Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
              frameBuffers(0).begin()
              Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT)
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
              */
            } catch {
              case e: Throwable => e.printStackTrace(); renderFailed = true
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
