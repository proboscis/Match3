package com.glyph.scala.game.table

import com.glyph.scala.lib.util.tile.TileRegionGenerator
import com.badlogic.gdx.scenes.scene2d.ui.{Button, Image, Table}
import com.glyph.scala.lib.util.actor.Touchable
import com.glyph.scala.lib.math.Vec2

/**
 * @author glyph
 */
class TileViewer(filename: String, tw: Int, th: Int) extends Table {
  val regions = new TileRegionGenerator(filename, tw, th)
  for (row <- regions.tile) {
    for (region <- row) {
      add(new Image(region) with Touchable{
        onPressed = (pos:Vec2)=>{
          println(pos)
        }
      })
    }
    this.row()
  }
}
