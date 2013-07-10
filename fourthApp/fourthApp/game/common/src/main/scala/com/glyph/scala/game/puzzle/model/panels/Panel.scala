package com.glyph.scala.game.puzzle.model.panels

import util.Random

/**
 * @author glyph
 */
class Panel {
}

object Panel {
  private val rand = new Random()
  private val seed = List(
    () => new Fire,
    () => new Water,
    () => new Thunder,
    () => new Monster)
  class Element extends Panel
  class Fire extends Element

  class Water extends Element

  class Thunder extends Element

  class Monster extends Panel

  def random(): Panel = {
    seed(rand.nextInt(seed.size))()
  }
}
