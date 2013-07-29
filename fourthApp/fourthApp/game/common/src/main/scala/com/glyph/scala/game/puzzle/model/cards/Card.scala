package com.glyph.scala.game.puzzle.model.cards

import com.glyph.scala.game.puzzle.controller.PuzzleGameController

/**
 * @author glyph
 */
trait Card {
  def apply(controller:PuzzleGameController)
  //TODO 効果の実装
}
class Scanner extends Card{
  def apply(controller: PuzzleGameController) {
    controller.startScanSequence()
    controller.damage()
  }
}
class Meteor extends Card{
  def apply(controller: PuzzleGameController) {
    controller.destroy((0 to 5).map{i => (i,i)}:_*)
  }
}

