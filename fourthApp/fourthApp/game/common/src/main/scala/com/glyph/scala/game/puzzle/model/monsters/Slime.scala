package com.glyph.scala.game.puzzle.model.monsters

import com.glyph.scala.game.puzzle.controller.PuzzleGameController


/**
 * @author glyph
 */
class Slime extends Monster {
  /**
   * you cannot call end() inside this method or it will cause a stack over flow
   */
  override def onTurnStart(controller: PuzzleGameController) {
    super.onTurnStart(controller)
    turnEnd()
  }
  override def toString: String = "S"
  def atk: Int = 1
}