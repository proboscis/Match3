package com.glyph._scala.lib.libgdx.font

import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.{TextureRegion, BitmapFont}
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter

/**
 * @author glyph
 */
object FontUtil {
  //TODO use distance field font and custom shader for smooth font !
  //hope if the libgdx uses it by default
  def internalFont(name: String, size: Int) = {
    //TODO dispose generator after usage
    val generator = new FreeTypeFontGenerator(Gdx.files.internal(name))
    //val characters = 1 to 255 map (_.toChar) mkString
    val font = generator.generateFont(size)
    generator.dispose()
    font.getRegion.getTexture.setFilter(TextureFilter.Linear,TextureFilter.Linear)
    font
  }

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
