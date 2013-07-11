package com.glyph.scala.game.puzzle.screen

import com.glyph.scala.lib.libgdx.screen.{TabledScreen, StagedScreen}
import com.glyph.scala.ScalaGame
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.glyph.scala.game.puzzle.view.PuzzleGameView
import com.glyph.scala.game.puzzle.model.Game
import com.glyph.scala.game.puzzle.controller.PuzzleGameController
import com.glyph.scala.lib.util.updatable.Updatables
import com.glyph.scala.lib.libgdx.actor.Scissor
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.{Color, FPSLogger, GL20}

/**
 * @author glyph
 */
class PuzzleScreen extends TabledScreen {
  //TODO ControllerはViewのイベントをModelに渡すためのもの。
  //TODO ビューの状態遷移はビューで、ゲームの状態（ターン等）はモデルクラスでやればよい。
  override val backgroundColor: Color = Color.DARK_GRAY
  def STAGE_WIDTH = ScalaGame.VIRTUAL_WIDTH
  def STAGE_HEIGHT = ScalaGame.VIRTUAL_HEIGHT
  def DEBUG: Boolean = true
  /*
  init values
   */
  val game = new Game //Model
  val gameController = new PuzzleGameController(game)//Controller
  val gameView = new PuzzleGameView(game,gameController) with Scissor//View
  /*
   init layout
   */
  root.add(gameView).fill().expand()
  root.invalidate()
  root.layout()
  /*
  init game
   */
  game.initialize()
  val fps = new FPSLogger
}
