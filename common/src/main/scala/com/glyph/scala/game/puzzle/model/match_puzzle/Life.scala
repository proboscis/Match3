package com.glyph.scala.game.puzzle.model.match_puzzle

import com.glyph.scala.game.puzzle.controller.PuzzleGameController
import com.glyph.scala.lib.puzzle.Match3
import Match3.Panel
import com.glyph.scala.lib.puzzle.Match3

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
