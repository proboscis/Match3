package com.glyph.scala.game.puzzle.view

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.glyph.scala.game.puzzle.model.Game
import com.glyph.scala.ScalaGame
import com.badlogic.gdx.scenes.scene2d.Touchable

/**
 * @author glyph
 */
class PuzzleGameView(game: Game) extends Table {
  import ScalaGame._
  val cardView = new CardView(game.deck)
  val puzzleView = new PuzzleView(game.puzzle)
  add(puzzleView).size(VIRTUAL_WIDTH,VIRTUAL_WIDTH)
  row()
  add(cardView).size(VIRTUAL_WIDTH,VIRTUAL_WIDTH/5f*1.618f)
  debug()
}
