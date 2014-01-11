package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.screen.{ConfiguredScreen, ScreenBuilder}
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.{Gdx, Screen}
import com.glyph.scala.lib.libgdx.gl.ShaderHandler
import com.badlogic.gdx.graphics.glutils.{ShaderProgram, ImmediateModeRenderer, ImmediateModeRenderer20}
import com.badlogic.gdx.graphics._
import com.glyph.scala.game.Glyphs
import Glyphs._
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.glyph.scala.lib.util.Logging
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.utils.{BufferUtils, GdxRuntimeException, Disposable}
import com.badlogic.gdx.graphics.Texture.{TextureFilter, TextureWrap}
import com.badlogic.gdx.Application.ApplicationType
import java.nio.{FloatBuffer, ByteOrder, ByteBuffer}
import com.badlogic.gdx.scenes.scene2d.{InputEvent, InputListener, Actor}
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2

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
    val powerShaderHandler = new ShaderHandler("shader/power.vert", "shader/power.frag")
    val densityShaderHandler = new ShaderHandler("shader/density.vert", "shader/density.frag")
    val pointRenderer = new ImmediateModeRenderer20(1000, false, true, 0)
    val textureRenderer = new ImmediateModeRenderer20(1000, false, true, 1)
    //TODO this format defines whether the texture2D in glsl returns clamped data or not
    val PARTICLE_COUNT_W = 100
    val PARTICLE_COUNT_H = 100
    val MAP_RESOLUTION = 200
    val RADIUS = 10
    val frameBuffers = Array(1 to 2 map (_ => new FrameBuffer(FloatTexture(PARTICLE_COUNT_W, PARTICLE_COUNT_H), false)): _*)
    val powerMap = new FrameBuffer(FloatTexture(MAP_RESOLUTION, MAP_RESOLUTION), false)
    val densityMap = new FrameBuffer(FloatTexture(MAP_RESOLUTION, MAP_RESOLUTION), false)
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
      val vertices = (0 until PARTICLE_COUNT_W * PARTICLE_COUNT_H map (i => Array((i / PARTICLE_COUNT_W) / PARTICLE_COUNT_W.toFloat, (i % PARTICLE_COUNT_W) / PARTICLE_COUNT_H.toFloat))).flatten
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

    val centerTable = new Table
    centerTable.debug()
    val leftTable = new Table
    leftTable.debug()
    val rightTable = new Table
    rightTable.debug()
    val texture: Texture = "data/sword.png".fromAssets
    val dummyTexture: Texture = "data/dummy.png".fromAssets
    val particleTexture: Texture = "data/particle.png".fromAssets
    val forceTexture = createForceFieldTexture(MAP_RESOLUTION)
    val forceActor = new TextureRenderActor(forceTexture)
    val powerActor = new TextureRenderActor(powerMap.getColorBufferTexture)
    val textureActors = ((frameBuffers :+ powerMap :+ densityMap).map(_.getColorBufferTexture) :+ forceTexture).map(new TextureRenderActor(_))
    textureActors foreach {
      actor => rightTable.add(actor).fill.expand.row
    }

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
    val renderFunction = pointShaderHandler.shader ~ feedbackShaderHandler.shader ~ powerShaderHandler.shader ~ densityShaderHandler.shader map {
      case pointShaderOpt ~ feedbackShaderOpt ~ powerShaderOpt ~ densityShaderOpt => {
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
              val pointShader = pointShaderOpt.get
              val feedbackShader = feedbackShaderOpt.get
              val powerShader = powerShaderOpt.get
              val densityShader = densityShaderOpt.get
              val pr = pointRenderer
              val tr = textureRenderer
              camera.update()
              val i1 = if (swap) 0 else 1
              val i2 = if (swap) 1 else 0

              /**
               * 力場をgpuでフレームバッファに加算合成することで作ることができるぞ！
               */
              //TODO viscocity etc..
              val gl = Gdx.gl
              import gl._

              {
                //update power map
                import powerShader._
                begin()
                //stage.getCamera.position.set(0,0,0)
                frameBuffers(i1).getColorBufferTexture.bind(0)
                forceTexture.bind(1)
                setUniformMatrix("u_projModelView", stage.getCamera.combined)
                setUniformi("u_sampler0", 0)
                setUniformi("u_sampler1", 1)
                setUniformf("u_pointSize", MAP_RESOLUTION/RADIUS)
                import GL20._
                glDisable(GL_DEPTH_TEST)
                glEnable(GL_BLEND)
                glBlendFunc(GL_ONE, GL_ONE)
                //Gdx.gl20.glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE, GL_SRC_ALPHA, GL_ONE)
                powerMap.begin()
                glClear(GL_COLOR_BUFFER_BIT)
                particleVertices.render(powerShader, GL_POINTS)
                powerMap.end()
                end()
              }

              {
                //update density map
                import densityShader._
                begin()
                //stage.getCamera.position.set(0,0,0)
                frameBuffers(i1).getColorBufferTexture.bind(0)
                forceTexture.bind(1)
                setUniformMatrix("u_projModelView", stage.getCamera.combined)
                setUniformi("u_sampler0", 0)
                setUniformi("u_sampler1", 1)
                setUniformf("u_pointSize", MAP_RESOLUTION/RADIUS)
                import GL20._
                glDisable(GL_DEPTH_TEST)
                glEnable(GL_BLEND)
                glBlendFunc(GL_ONE, GL_ONE)
                //Gdx.gl20.glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE, GL_SRC_ALPHA, GL_ONE)
                densityMap.begin()
                glClear(GL_COLOR_BUFFER_BIT)
                particleVertices.render(densityShader, GL_POINTS)
                densityMap.end()
                end()
              }

              {
                import feedbackShader._
                Gdx.gl.glDisable(GL10.GL_BLEND)
                //ping-pong the vel pos texture
                begin()
                frameBuffers(i1).getColorBufferTexture.bind(0) //this costs nothing.
                powerMap.getColorBufferTexture.bind(1)
                densityMap.getColorBufferTexture.bind(2)
                //forceTexture.bind(1)
                setUniformMatrix("u_projModelView", camera.combined)
                setUniformi("u_sampler0", 0)
                setUniformi("u_sampler1", 1)
                setUniformi("u_sampler2", 2)
                setUniformf("u_dt", 0.016f)
                setUniformi("u_state", 0)
                setUniformi("u_init", if (init) 1 else 0)
                setUniformf("mouse", mouseX, mouseY)
                frameBuffers(i2).begin()
                rect.render(feedbackShader, GL10.GL_TRIANGLE_STRIP)
                frameBuffers(i2).end()
                end()
                swap = !swap
                init = false
              }

              {
                // now draw particles with vel_pos texture
                Gdx.gl.glEnable(GL10.GL_BLEND)
                Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
                pointShader.begin()
                //stage.getCamera.position.set(0,0,0)
                frameBuffers(i2).getColorBufferTexture.bind(0)
                particleTexture.bind(1)
                //forceTexture.bind(1)
                powerMap.getColorBufferTexture.bind(2)
                pointShader.setUniformMatrix("u_projModelView", stage.getCamera.combined)
                pointShader.setUniformi("u_sampler0", 0)
                pointShader.setUniformi("u_sampler1", 1)
                pointShader.setUniformi("u_sampler2", 2)
                pointShader.setUniformf("u_pointSize", 10)
                particleVertices.render(pointShader, GL10.GL_POINTS)
                pointShader.end()
              }
            } catch {
              case e: Throwable => e.printStackTrace(); renderFailed = true
            }
          }
        }
      }
    }

    root.add(leftTable).fill.expand(142, 90)
    root.add(rightTable).fill.expand(18, 90)
    powerActor.toFront()

    override def render(delta: Float): Unit = {
      super.render(delta)
      renderFunction()()
      Gdx.gl.glActiveTexture(GL10.GL_TEXTURE0) // <= this is required
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

  def createForceField(size: Int): FloatBuffer = {
    val result = BufferUtils.newFloatBuffer(size * size * 4)
    val center = new Vector2(size / 2, size / 2)
    val tmp = new Vector2()
    val data = new Array[Float](size * size * 4)
    var x = 0
    while (x < size) {
      var y = 0
      while (y < size) {
        val idx = (y * size + x) * 4
        tmp.set(x, y).sub(center)
        val l2 = tmp.len2()
        val amp = if (l2 > size / 2 * size / 2 || l2 < 0.0000001) 0 else 1f / l2
        tmp.nor()
        data(idx) = tmp.x * amp
        data(idx + 1) = tmp.y * amp
        data(idx + 2) = amp
        data(idx + 3) = 1f
        y += 1
      }
      x += 1
    }
    result.put(data)
    result.flip()
    result
  }

  def createForceFieldTexture(size: Int): FloatTexture = new FloatTexture(size, size, createForceField(size))
}

class FrameBuffer(val colorTexture: GLTexture, val hasDepth: Boolean) extends Disposable {

  import FrameBuffer._

  var framebufferHandle = 0
  var depthbufferHandle = 0
  val width = colorTexture.getWidth
  val height = colorTexture.getHeight
  build()

  def setupTexture() {
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

class FloatTexture(val width: Int, val height: Int, buffer: FloatBuffer) extends GLTexture(GL10.GL_TEXTURE_2D, GLTextureWrapper.createGLHandle()) {
  load()

  def load() {
    //load
    bind()
    //uploadImageData
    //glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_HALF_FLOAT_OES, NULL) <= this seem to work on ios
    Gdx.gl.glTexImage2D(
      // target, level, internal format, width, height
      GL10.GL_TEXTURE_2D, /*RGBA32F_ARB 0x8814*/ 0, 0x8814, width, height,
      // border, data format, data type, pixels
      0, GL10.GL_RGBA, GL10.GL_FLOAT, buffer
    )
    println(Gdx.gl.glGetString(GL10.GL_EXTENSIONS))
    setFilter(minFilter, magFilter)
    setWrap(uWrap, vWrap)
    Gdx.gl.glBindTexture(glTarget, 0)
  }

  //if managed, add managedtexture
  def getWidth: Int = width

  def getHeight: Int = height

  def getDepth: Int = 0

  def isManaged: Boolean = false

  def reload(): Unit = load()
}

object FloatTexture {
  def apply(width: Int, height: Int): FloatTexture = new FloatTexture(width, height, BufferUtils.newFloatBuffer(width * height * 4))
}