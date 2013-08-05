package com.glyph.scala.game.puzzle.model.puzzle

/**
 * @author glyph
 */
trait Panel{
  def matchTo(other:Panel):Boolean
}
