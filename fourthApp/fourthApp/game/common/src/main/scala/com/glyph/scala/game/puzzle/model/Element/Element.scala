package com.glyph.scala.game.puzzle.model.Element

import com.glyph.scala.game.puzzle.model.match_puzzle.Panel

/**
 * @author glyph
 */
trait Element extends Panel{
  def matchTo(other: Panel): Boolean = getClass == other.getClass
}
class Fire extends Element{
  override def toString: String = "F"
}
class Water extends Element{
  override def toString: String = "W"
}
class Thunder extends Element{
  override def toString: String = "T"
}


