package com.glyph.scala.game.puzzle.model.match_puzzle

import com.glyph.scala.lib.puzzle.Match3
import Match3.Panel
import com.glyph.scala.lib.puzzle.Match3

/**
 * @author glyph
 */
class Move extends Panel{
  def matchTo(other: Panel): Boolean = other.isInstanceOf[Move]
}
