package com.glyph.scala.game.puzzle.controller

import com.glyph.scala.game.puzzle.model.Game
import com.glyph.scala.game.puzzle.view.PuzzleGameView
import com.glyph.scala.lib.util.updatable.Updatables
import com.glyph.scala.lib.util.observer.Observing
import com.badlogic.gdx.scenes.scene2d.Touchable

/**
 * Receives events from view, and pass it to the game model
 * @author glyph
 */
class PuzzleGameController(game: Game, view: PuzzleGameView) extends Updatables with Observing {
  val cardViewController = new CardViewController(view.cardView, game, game.deck)
  val puzzleViewController = new PuzzleViewController(view.puzzleView, game.puzzle)
  observe(puzzleViewController.scanning) {
    case true => view.cardView.setTouchable(Touchable.disabled)
    case false => view.cardView.setTouchable(Touchable.enabled)
  }
  add(cardViewController)
  add(puzzleViewController)
}
