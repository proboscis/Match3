package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.screen.{ConfiguredScreen, ScreenBuilder}
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.{Gdx, Screen}
import com.glyph.scala.lib.libgdx.gl.{GlyphFrameBuffer, FloatTexture, ShaderHandler}
import com.badlogic.gdx.graphics.glutils.{ShaderProgram, ImmediateModeRenderer, ImmediateModeRenderer20}
import com.badlogic.gdx.graphics._
import com.glyph.scala.game.Glyphs
import Glyphs._
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.glyph.scala.lib.util.Logging
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.utils.BufferUtils
import java.nio.FloatBuffer
import com.badlogic.gdx.scenes.scene2d.{InputEvent, InputListener, Actor}
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.{Interpolation, MathUtils, Vector2}
import scalaz._
import Scalaz._
import com.glyph.util.Noise

/**
 * @author glyph
 */
class TransformFeedback extends ScreenBuilder with Logging {
  def requirements: Set[(Class[_], Seq[String])] = Set(
    classOf[Texture] -> Seq("data/sword.png", "data/dummy.png", "data/particle.png","data/penmark.jpg")
  )

  def create(implicit assetManager: AssetManager): Screen = new ConfiguredScreen {
    autoClearScreen = false
    ShaderProgram.pedantic = false
    println(Gdx.gl.glGetString(GL10.GL_EXTENSIONS))

    override def DEBUG: Boolean = false

    override def STAGE_WIDTH: Int = 960

    override def STAGE_HEIGHT: Int = 540

    val pointShaderHandler = new ShaderHandler("shader/point.vert", "shader/point.frag")
    val feedbackShaderHandler = new ShaderHandler("shader/feedback.vert", "shader/feedback.frag")
    val textureRenderer = new ImmediateModeRenderer20(1000, false, true, 1)
    //TODO this format defines whether the texture2D in glsl returns clamped data or not
    val PARTICLE_COUNT_W = 1000
    val PARTICLE_COUNT_H = 1000
    val MAP_RESOLUTION = 200
    val frameBuffers = Array(1 to 2 map (_ => new GlyphFrameBuffer(FloatTexture(PARTICLE_COUNT_W, PARTICLE_COUNT_H), false)): _*)
    //TODO extend FrameBuffer to use my own Texture which internally uses FloatBuffer
    //TODO extends Texture to fool the FrameBuffer class
    val a_position = new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE)
    val a_texCoord = VertexAttribute.TexCoords(0)
    val rect = new Mesh(true, 4, 0, a_position, a_texCoord)
    rect.setVertices(rectangle(-0.5f, -0.5f, 1f, 1f))
    val particleVertices = new Mesh(true, PARTICLE_COUNT_W * PARTICLE_COUNT_H, 0, a_position)

    def rectangle(x: Float, y: Float, w: Float, h: Float): Array[Float] = Array[Float](
      x, y, 0, 0,
      x, y + h, 0, 1,
      x + w, y, 1, 0,
      x + w, y + h, 1, 1
    )

    {
      //init particleVertices
      val vertices = (0 until PARTICLE_COUNT_W * PARTICLE_COUNT_H map (i => Array((i % PARTICLE_COUNT_W) / PARTICLE_COUNT_W.toFloat, (i / PARTICLE_COUNT_W) / PARTICLE_COUNT_H.toFloat))).flatten
      particleVertices.setVertices(Array(vertices: _*))
      println(vertices.size)
    }

    class TextureRenderActor(tex: GLTexture) extends Actor {
      override def draw(batch: Batch, parentAlpha: Float): Unit = {
        super.draw(batch, parentAlpha)
        batch.end()
        val tr = textureRenderer
        tr.begin(batch.getProjectionMatrix, GL10.GL_TRIANGLE_STRIP)
        tex.bind()
        texturedRect(tr)(Color.WHITE, getX, getY, getWidth, getHeight)
        tr.end()
        batch.begin()
      }
    }

    val leftTable = new Table
    leftTable.debug()
    val rightTable = new Table
    rightTable.debug()
    val texture: Texture = "data/sword.png".fromAssets
    val dummyTexture: Texture = "data/dummy.png".fromAssets
    val particleTexture: Texture = "data/particle.png".fromAssets
    val penmark:Texture = "data/penmark.jpg".fromAssets
    import Noise._

    val RESOLUTION = 400
    val filledPerlins = 1 to 2 map (_ => generatePerlinNoise(2,8)(f => 1f/Math.sqrt(f).toFloat)) map (fill2D(RESOLUTION,RESOLUTION)(_))
    val perlinTexture = new FloatTexture(RESOLUTION,RESOLUTION,newFloatBuffer(floatNDToFloatMD(filledPerlins,4)))
    val textureActors = (frameBuffers.map(_.getColorBufferTexture) :+perlinTexture ).map(new TextureRenderActor(_))
    textureActors foreach {
      actor => rightTable.add(actor).fill.expand.row
    }

    //root.add(leftTable).fill.expand(130, 90)
    //root.add(rightTable).fill.expand(30, 90)

    var mouseX = 0f
    var mouseY = 0f
    stage.addListener(new InputListener {
      override def mouseMoved(event: InputEvent, x: Float, y: Float): Boolean = {
        super.mouseMoved(event, x, y)
        mouseX = x
        mouseY = y
        true
      }
    })


    val camera = new OrthographicCamera(1, 1)
    camera.update()
    val renderFunction = pointShaderHandler.shader ~ feedbackShaderHandler.shader  map {
      case pointShaderOpt ~ feedbackShaderOpt=> {
        var renderFailed = false
        var swap = false
        var init = true
        var elapsedTime = 0f
        () => {
          if (!renderFailed && pointShaderOpt.isDefined && feedbackShaderOpt.isDefined) {
            try {
              elapsedTime += 0.016f;
              val gl = Gdx.gl
              import gl._
              import GL20._
              Gdx.gl20.glEnable(GL11.GL_POINT_SPRITE_OES) //this is required!!!
              //// http://stackoverflow.com/questions/13213227/gl-pointcoord-has-incorrect-uninitialized-value
              Gdx.gl.glEnable(GL_VERTEX_PROGRAM_POINT_SIZE)
              //log("drawing")
              val pointShader = pointShaderOpt.get
              val feedbackShader = feedbackShaderOpt.get
              camera.update()
              val i1 = if (swap) 0 else 1
              val i2 = if (swap) 1 else 0


              {
                import feedbackShader._
                glDisable(GL_BLEND)
                //ping-ponwg the vel pos texture
                begin()
                frameBuffers(i1).getColorBufferTexture.bind(0) //this costs nothing.
                perlinTexture.bind(1)

                setUniformMatrix("u_projModelView", camera.combined)
                setUniformi("u_sampler0", 0)
                setUniformi("u_sampler1", 1)
                setUniformi("u_sampler2", 2)
                setUniformf("u_dt", 0.016f)
                setUniformi("u_state", if (init) 1 else 0)
                setUniformf("mouse", mouseX, mouseY)
                frameBuffers(i2).begin()
                rect.render(feedbackShader, GL_TRIANGLE_STRIP)
                frameBuffers(i2).end()
                end()
                swap = !swap
                init = false
              }

              {
                // now draw particles with vel_pos texture
                glEnable(GL_BLEND)
                glBlendFunc(GL_SRC_ALPHA, GL_ONE)
                pointShader.begin()
                //stage.getCamera.position.set(0,0,0)
                frameBuffers(i2).getColorBufferTexture.bind(0)
                penmark.bind(1)
                pointShader.setUniformMatrix("u_projModelView", stage.getCamera.combined)
                pointShader.setUniformi("u_sampler0", 0)
                pointShader.setUniformi("u_sampler1", 1)
                pointShader.setUniformi("u_sampler2", 2)
                pointShader.setUniformf("u_pointSize", 1)
                pointShader.setUniformf("u_et",elapsedTime)
                particleVertices.render(pointShader, GL_POINTS)
                pointShader.end()
              }
            } catch {
              case e: Throwable => e.printStackTrace(); renderFailed = true
            }
          }
        }
      }
    }

    override def render(delta: Float): Unit = {
      clearScreen()
      renderFunction()()
      Gdx.gl.glActiveTexture(GL10.GL_TEXTURE0) // <= this is required for sprite batch
      super.render(delta)
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
  }

  def newFloatBuffer(data: Array[Float]): FloatBuffer = {
    val result = BufferUtils.newFloatBuffer(data.length)
    result.put(data)
    result.flip()
    result
  }
}




