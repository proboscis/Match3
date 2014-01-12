package com.glyph.scala.lib.libgdx.gl

import com.badlogic.gdx.graphics.{GL20, GLTexture}
import com.badlogic.gdx.utils.{Disposable, GdxRuntimeException, BufferUtils}
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture.{TextureFilter, TextureWrap}
import com.badlogic.gdx.Application.ApplicationType
import java.nio.{ByteOrder, ByteBuffer}

/**
 * @author glyph
 */
class GlyphFrameBuffer(val colorTexture: GLTexture, val hasDepth: Boolean) extends Disposable {

  import GlyphFrameBuffer._

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
object GlyphFrameBuffer {
  val buffers = new java.util.HashMap[com.badlogic.gdx.Application, com.badlogic.gdx.utils.Array[GlyphFrameBuffer]]
  var defaultFramebufferHandle = 0
  var defaultFramebufferHandleInitialized = false

  def addManagedFrameBuffer(app: com.badlogic.gdx.Application, buf: GlyphFrameBuffer) {
    var resources = buffers.get(app)
    if (resources == null) {
      resources = new com.badlogic.gdx.utils.Array[GlyphFrameBuffer]()
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
