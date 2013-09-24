package com.glyph.scala.game.puzzle.model.match_puzzle

/**
 * @author glyph
 */
class Life extends Panel{
  def matchTo(other: Panel): Boolean = other.isInstanceOf[Life]

  override def toString: String = "L"
}
