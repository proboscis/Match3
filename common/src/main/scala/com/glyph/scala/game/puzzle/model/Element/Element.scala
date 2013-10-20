package com.glyph.scala.game.puzzle.model.Element

import com.glyph.scala.game.puzzle.model.match_puzzle.{Match3, DestroyEffect}
import com.glyph.scala.game.puzzle.controller.PuzzleGameController
import Match3.Panel

/**
 * @author glyph
 */
trait Element extends Panel with DestroyEffect{
  def matchTo(other: Panel): Boolean = getClass == other.getClass
}
class Fire extends Element{
  override def toString: String = "F"

  def onDestroy(controller: PuzzleGameController) {
    controller.game.player.fireMana() += 1
  }
}
class Water extends Element{
  override def toString: String = "W"

  def onDestroy(controller: PuzzleGameController) {
    controller.game.player.waterMana() += 1
  }
}
class Thunder extends Element{
  override def toString: String = "T"

  def onDestroy(controller: PuzzleGameController) {
    controller.game.player.thunderMana() += 1
  }
}


