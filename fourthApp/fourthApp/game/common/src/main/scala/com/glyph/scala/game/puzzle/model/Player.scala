package com.glyph.scala.game.puzzle.model

import com.glyph.scala.lib.util.reactive.Var


/**
 * @author glyph
 */
class Player {
  val hp = Var(100)
  val position = Var(1)
}
