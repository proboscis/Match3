package com.glyph.scala.game.puzzle.model.match_puzzle

import com.glyph.scala.game.puzzle.controller.PuzzleGameController

/**
 * @author glyph
 */
class Life extends Panel with DestroyEffect{
  def matchTo(other: Panel): Boolean = other.isInstanceOf[Life]

  override def toString: String = "L"

  def onDestroy(controller: PuzzleGameController) {
    controller.game.player.hp() += 5
  }
}
