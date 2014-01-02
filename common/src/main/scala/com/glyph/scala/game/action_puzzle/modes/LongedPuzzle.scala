package com.glyph.scala.game.action_puzzle.modes

import com.glyph.scala.game.action_puzzle.ActionPuzzle
import com.badlogic.gdx.math.MathUtils

/**
 * @author glyph
 */
class LongedPuzzle {
  val puzzle = new ActionPuzzle(6,6,()=>MathUtils.random(0,5),(_:Int)==(_:Int))
}
