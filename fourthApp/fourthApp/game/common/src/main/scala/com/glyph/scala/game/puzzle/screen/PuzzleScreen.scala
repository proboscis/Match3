package com.glyph.scala.game.puzzle.screen

import com.glyph.scala.lib.libgdx.screen.TabledScreen
import com.glyph.scala.ScalaGame
import com.glyph.scala.game.puzzle.model.Game
import com.glyph.scala.game.puzzle.controller.PuzzleGameController
import com.glyph.scala.lib.libgdx.actor.Scissor
import com.badlogic.gdx.graphics.{Color, FPSLogger}
import com.glyph.scala.lib.util.json.DepreactedRJSON
import com.glyph.scala.lib.libgdx.reactive.GdxFile
import com.glyph.scala.lib.util.hfsm.{HReactiveEmpty, ReactiveHState}
import com.glyph.scala.lib.util.reactive.Var
import com.glyph.scala.game.puzzle.view.PuzzleGameView

/**
 * @author glyph
 */
class PuzzleScreen extends TabledScreen {
  val config = DepreactedRJSON(GdxFile("json/puzzleScreenConfig.json"))
  val debug = config.debug.toBoolean

  //TODO ControllerはViewのイベントをModelに渡すためのもの。
  //TODO ビューの状態遷移はビューで、ゲームの状態（ターン等）はモデルクラスでやればよい。
  override val backgroundColor: Color = Color.DARK_GRAY

  def STAGE_WIDTH = ScalaGame.VIRTUAL_WIDTH

  def STAGE_HEIGHT = ScalaGame.VIRTUAL_HEIGHT

  def DEBUG: Boolean = if (debug == null) false else debug()

  //Model
  val game = new Game(GdxFile(_))
  //Controller
  val gameController = new PuzzleGameController(game)
  //TODO make View Controller
  //View
  val gameView = new PuzzleGameView(game,gameController) with Scissor
  /*
   init layout
   */
  root.add(gameView).fill().expand()
  root.invalidate()
  root.layout()
  /*
  init game
   */
  gameController.initialize()
  val fps = new FPSLogger
}
