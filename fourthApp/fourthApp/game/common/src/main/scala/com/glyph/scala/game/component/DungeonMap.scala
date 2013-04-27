package com.glyph.scala.game.component

import com.glyph.scala.Glyph

/**
 * @author glyph
 */
class DungeonMap {
  val map = Seq.fill(100)(1)
  Glyph.log("DungeonMap",map+"")
}
