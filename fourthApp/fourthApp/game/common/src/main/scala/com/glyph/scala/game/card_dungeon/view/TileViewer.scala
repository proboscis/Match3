package com.glyph.scala.game.view

import com.badlogic.gdx.scenes.scene2d.ui.{Image, Table}
import com.glyph.scala.lib.math.Vec2
import com.glyph.scala.lib.libgdx.TileRegionGenerator
import com.glyph.scala.lib.libgdx.actor.Touchable

/**
 * @author glyph
 */
class TileViewer(filename: String, tw: Int, th: Int) extends Table {
  val regions = new TileRegionGenerator(filename, tw, th)
  for (row <- regions.tile) {
    for (region <- row) {
      add(new Image(region) with Touchable {
      })
    }
    this.row()
  }
}
