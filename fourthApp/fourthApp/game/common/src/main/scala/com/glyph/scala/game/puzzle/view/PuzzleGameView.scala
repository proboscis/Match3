package com.glyph.scala.game.puzzle.view

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.glyph.scala.game.puzzle.model.Game
import com.glyph.scala.ScalaGame

/**
 * @author glyph
 */
class PuzzleGameView(game: Game) extends Table {
  val cardView = new CardView(game.deck)
  val puzzleView = new PuzzleView(game.puzzle)
  add(puzzleView).size(ScalaGame.VIRTUAL_WIDTH,ScalaGame.VIRTUAL_WIDTH)
  row()
  add(cardView).expand(1, 1).fill
  debug()
}
