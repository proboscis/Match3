package com.glyph.scala.game.puzzle.model.Element

import com.glyph.scala.game.puzzle.model.puzzle.Panel

/**
 * @author glyph
 */
trait Element extends Panel{
  def matchTo(other: Panel): Boolean = getClass == other.getClass
}
class Fire extends Element
class Water extends Element
class Thunder extends Element


