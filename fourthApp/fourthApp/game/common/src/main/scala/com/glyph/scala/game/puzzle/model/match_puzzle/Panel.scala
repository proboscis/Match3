package com.glyph.scala.game.puzzle.model.match_puzzle

/**
 * @author glyph
 */
trait Panel{
  def matchTo(other:Panel):Boolean
}
