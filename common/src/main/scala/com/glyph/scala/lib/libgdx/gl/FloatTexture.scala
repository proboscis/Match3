package com.glyph.scala.lib.libgdx.gl

import java.nio.FloatBuffer
import com.badlogic.gdx.graphics.{GL10, GLTexture}
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.BufferUtils
import com.glyph.scala.test.GLTextureWrapper

/**
 * @author glyph
 */
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