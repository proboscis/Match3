package com.glyph._scala.lib.libgdx.font

import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.{TextureRegion, BitmapFont}

/**
 * @author glyph
 */
object FontUtil {
  def internalFont(name: String, size: Int) = new FreeTypeFontGenerator(Gdx.files.internal(name)).generateFont(size)

  def charToRegion(font: BitmapFont)(c: Char)={
    val glyph = font.getData.getGlyph(c)
    val texture = font.getRegion.getTexture
    import glyph._
    new TextureRegion(texture, srcX, srcY, width, height)
  }

  def fontToRegionMap(font: BitmapFont)(characters: Seq[Char]) = (characters map {
    c => c -> charToRegion(font)(c)
  }).toMap

  def fontToLazyRegionMap(font: BitmapFont) = Map[Char, TextureRegion]() withDefault charToRegion(font)
}
