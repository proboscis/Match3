package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.screen.{ConfiguredScreen, ScreenBuilder}
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.{Gdx, Screen}
import com.glyph.scala.lib.libgdx.gl.ShaderHandler
import com.badlogic.gdx.graphics.glutils.{ImmediateModeRenderer20, ShaderProgram, ImmediateModeRenderer}
import com.badlogic.gdx.graphics.Pixmap.Format
import com.badlogic.gdx.graphics._
import com.glyph.scala.game.Glyphs
import Glyphs._
import scalaz._
import Scalaz._
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.glyph.scala.lib.libgdx.actor.SpriteActor
import com.glyph.scala.lib.util.Logging
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.utils.{BufferUtils, GdxRuntimeException, Disposable}
import com.badlogic.gdx.graphics.Texture.{TextureFilter, TextureWrap}
import com.badlogic.gdx.Application.ApplicationType
import java.nio.{ByteOrder, ByteBuffer}

/**
 * @author glyph
 */
class TransformFeedback extends ScreenBuilder with Logging {
  def requirements: Set[(Class[_], Seq[String])] = Set(
    classOf[Texture] -> Seq("data/sword.png", "data/dummy.png", "data/particle.png")
  )

  def create(implicit assetManager: AssetManager): Screen = new ConfiguredScreen {
    ShaderProgram.pedantic = false

    override def STAGE_WIDTH: Int = 960

    override def STAGE_HEIGHT: Int = 540

    val pointShaderHandler = new ShaderHandler("shader/point.vert", "shader/point.frag")
    val feedbackShaderHandler = new ShaderHandler("shader/feedback.vert", "shader/feedback.frag")
    val pointRenderer = new ImmediateModeRenderer20(1000, false, true, 0)
    val textureRenderer = new ImmediateModeRenderer20(1000, false, true, 1)
    //TODO this format defines whether the texture2D in glsl returns clamped data or not
    val frameBuffers = Array(1 to 2 map (_ => new FrameBuffer(Format.RGBA8888, 16, 16, false)): _*)
    //TODO extend FrameBuffer to use my own Texture which internally uses FloatBuffer
    //TODO extends Texture to fool the FrameBuffer class
    val a_position = new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE)
    val a_texCoord = VertexAttribute.TexCoords(0)
    val rect = new Mesh(true, 4, 0, a_position, a_texCoord)
    val particleVertices = new Mesh(true, 16, 0, a_position) {
      // init rectangle mesh
      val (x, y, w, h) = (-0.5f, -0.5f, 1f, 1f)
      rect.setVertices(Array[Float](
        x, y, 0, 0,
        x, y + h, 0, 1,
        x + w, y, 1, 0,
        x + w, y + h, 1, 1
      ))
    }

    {
      //init particleVertices
      val vertices = (0 until 16 map (i => Array((i / 4).toFloat, (i % 4).toFloat))).flatten
      particleVertices.setVertices(Array(vertices: _*))
    }

    val camera = new OrthographicCamera(1, 1)
    camera.update()

    val frameBufferActors = frameBuffers map (_.getColorBufferTexture |> SpriteActor.apply)

    val leftTable = new Table
    leftTable.debug()
    val rightTable = new Table
    rightTable.debug()
    val texture: Texture = "data/sword.png".fromAssets
    val dummyTexture: Texture = "data/dummy.png".fromAssets
    val particleTexture: Texture = "data/particle.png".fromAssets
    val SRC_FUNC: Int = GL10.GL_SRC_ALPHA
    val DST_FUNC: Int = GL10.GL_ONE
    frameBufferActors foreach {
      a => rightTable.add(a).fill.expand.row
    }
    root.add(leftTable).fill.expand
    root.add(rightTable).fill.expand

    val default = ImmediateModeRenderer20.createDefaultShader(false, true, 0)
    val renderFunction = pointShaderHandler.shader ~ feedbackShaderHandler.shader map {
      case pointShaderOpt ~ feedbackShaderOpt => {
        var renderFailed = false
        var swap = false
        var init = true
        () => {
          if (!renderFailed && pointShaderOpt.isDefined && feedbackShaderOpt.isDefined) {
            try {
              Gdx.gl20.glEnable(GL11.GL_POINT_SPRITE_OES) //this is required!!!
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

              {
                //ping-pong the vel pos texture
                val i1 = if (swap) 0 else 1
                val i2 = if (swap) 1 else 0
                fs.begin()
                frameBuffers(i1).getColorBufferTexture.bind(0) //this costs nothing.
                fs.setUniformMatrix("u_projModelView", camera.combined)
                fs.setUniformi("u_sampler0", 0)
                fs.setUniformf("u_dt", Gdx.graphics.getDeltaTime)
                fs.setUniformi("u_state", 0)
                fs.setUniformi("u_init", if (init) 1 else 0)
                frameBuffers(i2).begin()
                rect.render(fs, GL10.GL_TRIANGLE_STRIP)
                frameBuffers(i2).end()
                fs.end()
                swap = !swap
                init = false
              }

              {
                // now draw particles with vel_pos texture
                ps.begin()
                //stage.getCamera.position.set(0,0,0)
                ps.setUniformMatrix("u_projModelView", stage.getCamera.combined)
                ps.setUniformi("u_sampler0", 0) //assuming the texture is already bound
                particleVertices.render(ps, GL10.GL_POINTS)
                ps.end()
              }

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

class FrameBuffer(val format: Pixmap.Format, val width: Int, val height: Int, val hasDepth: Boolean) extends Disposable {

  import FrameBuffer._

  var colorTexture: Texture = null
  var framebufferHandle = 0
  var depthbufferHandle = 0
  build()

  def setupTexture() {
    colorTexture = new Texture(width, height, format)
    colorTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear)
    colorTexture.setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge)
  }

  def build() {
    if (!Gdx.graphics.isGL20Available) throw new GdxRuntimeException("GL2 is required.")

    val gl = Gdx.graphics.getGL20

    // iOS uses a different framebuffer handle! (not necessarily 0)
    if (!defaultFramebufferHandleInitialized) {
      defaultFramebufferHandleInitialized = true
      if (Gdx.app.getType == ApplicationType.iOS) {

        val intbuf = ByteBuffer.allocateDirect(16 * Integer.SIZE / 8).order(ByteOrder.nativeOrder()).asIntBuffer()
        gl.glGetIntegerv(GL20.GL_FRAMEBUFFER_BINDING, intbuf)
        defaultFramebufferHandle = intbuf.get(0)
      }
      else {
        defaultFramebufferHandle = 0
      }
    }

    setupTexture()

    val handle = BufferUtils.newIntBuffer(1)
    gl.glGenFramebuffers(1, handle)
    framebufferHandle = handle.get(0)

    if (hasDepth) {
      handle.clear()
      gl.glGenRenderbuffers(1, handle)
      depthbufferHandle = handle.get(0)
    }

    gl.glBindTexture(GL20.GL_TEXTURE_2D, colorTexture.getTextureObjectHandle)

    if (hasDepth) {
      gl.glBindRenderbuffer(GL20.GL_RENDERBUFFER, depthbufferHandle)
      gl.glRenderbufferStorage(GL20.GL_RENDERBUFFER, GL20.GL_DEPTH_COMPONENT16, colorTexture.getWidth,
        colorTexture.getHeight)
    }

    gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, framebufferHandle)
    gl.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER, GL20.GL_COLOR_ATTACHMENT0, GL20.GL_TEXTURE_2D,
      colorTexture.getTextureObjectHandle, 0)
    if (hasDepth) {
      gl.glFramebufferRenderbuffer(GL20.GL_FRAMEBUFFER, GL20.GL_DEPTH_ATTACHMENT, GL20.GL_RENDERBUFFER, depthbufferHandle)
    }
    val result = gl.glCheckFramebufferStatus(GL20.GL_FRAMEBUFFER)

    gl.glBindRenderbuffer(GL20.GL_RENDERBUFFER, 0)
    gl.glBindTexture(GL20.GL_TEXTURE_2D, 0)
    gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, defaultFramebufferHandle)

    if (result != GL20.GL_FRAMEBUFFER_COMPLETE) {
      colorTexture.dispose()
      if (hasDepth) {
        handle.clear()
        handle.put(depthbufferHandle)
        handle.flip()
        gl.glDeleteRenderbuffers(1, handle)
      }

      colorTexture.dispose()
      handle.clear()
      handle.put(framebufferHandle)
      handle.flip()
      gl.glDeleteFramebuffers(1, handle)

      if (result == GL20.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT)
        throw new IllegalStateException("frame buffer couldn't be constructed: incomplete attachment")
      if (result == GL20.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS)
        throw new IllegalStateException("frame buffer couldn't be constructed: incomplete dimensions")
      if (result == GL20.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT)
        throw new IllegalStateException("frame buffer couldn't be constructed: missing attachment")
      if (result == GL20.GL_FRAMEBUFFER_UNSUPPORTED)
        throw new IllegalStateException("frame buffer couldn't be constructed: unsupported combination of formats")
      throw new IllegalStateException("frame buffer couldn't be constructed: unknown error " + result)
    }
  }

  def dispose(): Unit = {
    val gl = Gdx.graphics.getGL20
    val handle = BufferUtils.newIntBuffer(1)

    colorTexture.dispose()
    if (hasDepth) {
      handle.put(depthbufferHandle)
      handle.flip()
      gl.glDeleteRenderbuffers(1, handle)
    }

    handle.clear()
    handle.put(framebufferHandle)
    handle.flip()
    gl.glDeleteFramebuffers(1, handle)

    if (buffers.get(Gdx.app) != null) buffers.get(Gdx.app).removeValue(this, true)
  }

  def begin() {
    Gdx.graphics.getGL20.glBindFramebuffer(GL20.GL_FRAMEBUFFER, framebufferHandle)
    Gdx.graphics.getGL20.glViewport(0, 0, colorTexture.getWidth, colorTexture.getHeight)
  }

  def end() {
    Gdx.graphics.getGL20().glBindFramebuffer(GL20.GL_FRAMEBUFFER, defaultFramebufferHandle)
    Gdx.graphics.getGL20().glViewport(0, 0, Gdx.graphics.getWidth, Gdx.graphics.getHeight)
  }

  def getColorBufferTexture = colorTexture
}

object FrameBuffer {
  val buffers = new java.util.HashMap[com.badlogic.gdx.Application, com.badlogic.gdx.utils.Array[FrameBuffer]]
  var defaultFramebufferHandle = 0
  var defaultFramebufferHandleInitialized = false

  def addManagedFrameBuffer(app: com.badlogic.gdx.Application, buf: FrameBuffer) {
    var resources = buffers.get(app)
    if (resources == null) {
      resources = new com.badlogic.gdx.utils.Array[FrameBuffer]()
    }
    resources.add(buf)
    buffers.put(app, resources)
  }

  def invalidateAllFrameBuffers(app: com.badlogic.gdx.Application) = buffers.remove(app)

  def getManagedStatus(builder: StringBuilder): StringBuilder = {
    builder.append("Managed buffers/app: { ")
    val itr = buffers.keySet().iterator()
    while (itr.hasNext) {
      builder.append(buffers.get(itr.next()).size)
      builder.append(" ")
    }
    builder.append("}")
  }

  def getManagedStatus: String = getManagedStatus(new StringBuilder).toString()
}