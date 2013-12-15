package com.glyph.scala.lib.libgdx.font

import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.{TextureRegion, BitmapFont}

/**
 * @author glyph
 */
object FontUtil{
  def internalFont(name:String,size:Int) = new FreeTypeFontGenerator(Gdx.files.internal(name)).generateFont(size)
  def fontToRegionMap(font:BitmapFont)(characters:Seq[Char]) = (characters map {
    c =>
      val glyph = font.getData.getGlyph(c)
      val texture = font.getRegion.getTexture
      import glyph._
      c -> new TextureRegion(texture, srcX, srcY, width, height)
  }).toMap
}
