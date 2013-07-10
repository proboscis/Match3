package com.glyph.scala.lib.libgdx

import com.badlogic.gdx.graphics.{Texture, Color, Pixmap}

/**
 * @author glyph
 */
object TextureUtil {
  private val image = new Pixmap(128, 128, Pixmap.Format.RGBA8888)
  image.setColor(Color.WHITE)
  image.fillRectangle(0, 0, image.getWidth, image.getHeight)
  private val texture = new Texture(image)

  def dummy = texture
}
