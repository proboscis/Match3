package com.glyph.scala.game.puzzle.controller

import com.glyph.scala.game.puzzle.model.Game
import com.glyph.scala.game.puzzle.view.PuzzleGameView
import com.glyph.scala.lib.util.updatable.Updatables

/**
 * Receives events from view, and pass it to the game model
 * @author glyph
 */
class PuzzleGameController(game: Game, view: PuzzleGameView) extends Updatables {
  val cardViewController = new CardViewController(view.cardView,game, game.deck)
  val puzzleViewController = new PuzzleViewController(view.puzzleView,game.puzzle)
  add(cardViewController)
  add(puzzleViewController)
}
