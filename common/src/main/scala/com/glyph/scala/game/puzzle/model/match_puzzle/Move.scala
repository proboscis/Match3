package com.glyph.scala.game.puzzle.model.match_puzzle
import Match3.Panel
/**
 * @author glyph
 */
class Move extends Panel{
  def matchTo(other: Panel): Boolean = other.isInstanceOf[Move]
}
