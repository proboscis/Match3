package com.glyph.scala.game.puzzle.screen

import com.glyph.scala.lib.libgdx.screen.TabledScreen
import com.glyph.scala.ScalaGame
import com.glyph.scala.game.puzzle.model.Game
import com.glyph.scala.game.puzzle.controller.PuzzleGameController
import com.glyph.scala.lib.libgdx.actor.Scissor
import com.badlogic.gdx.graphics.{Color, FPSLogger}
import com.glyph.scala.lib.libgdx.reactive.GdxFile
import com.glyph.scala.game.puzzle.view.PuzzleGameView
import com.glyph.scala.lib.util.json.RJSON

/**
 * @author glyph
 */
class PuzzleScreen extends TabledScreen {
  def configSrc = RJSON(GdxFile("json/gameConfig.json").getString)
  //TODO ControllerはViewのイベントをModelに渡すためのもの。
  //TODO ビューの状態遷移はビューで、ゲームの状態（ターン等）はモデルクラスでやればよい。
  override val backgroundColor: Color = Color.DARK_GRAY

  //Model
  val game = new Game(GdxFile(_))
  //Controller
  val gameController = new PuzzleGameController(game)
  //TODO make View Controller
  //View
  val gameView = new PuzzleGameView(game,gameController.deck,(STAGE_WIDTH,STAGE_HEIGHT)) with Scissor
  //gameView.setSize(STAGE_WIDTH,STAGE_HEIGHT)
  /*
   init layout
   */
  root.add(gameView).fill().expand()
  root.invalidate()
  root.layout()
  /*
  init game
   */
  gameController.destroyAnimation = gameView.puzzleView.destroyAnimation
  gameController.fillAnimation = gameView.puzzleView.fillAnimation
  gameController.damageAnimation = gameView.damageAnimation
  gameController.idleInput = gameView.idleInput

  gameController.initialize()
  val fps = new FPSLogger
}
