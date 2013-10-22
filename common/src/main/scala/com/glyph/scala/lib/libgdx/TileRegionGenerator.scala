package com.glyph.scala.lib.libgdx

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.Texture

/**
 * @author glyph
 */
class TileRegionGenerator(texture:Texture, tw: Int, th: Int) {
  val tile = TextureRegion.split(texture, tw, th)
  //tile.flatten.foreach(_.flip(false,true))
  val row = tile.size
  val column = tile(0).size

  def createRegion(index: Integer): TextureRegion = {
    val x = (index-1) % column
    val y = (index) / column
    val ni = x * row +y
    val r = tile(ni % row)(ni / row)
    new TextureRegion(r)
  }
}
