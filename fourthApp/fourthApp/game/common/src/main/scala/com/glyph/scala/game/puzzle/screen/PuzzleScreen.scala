package com.glyph.scala.game.puzzle.screen

import com.glyph.scala.lib.libgdx.screen.StagedScreen
import com.glyph.scala.ScalaGame
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.glyph.scala.game.puzzle.view.PuzzleGameView
import com.glyph.scala.game.puzzle.model.{Card, Game}
import com.glyph.scala.game.puzzle.controller.PuzzleGameController
import com.glyph.scala.lib.util.updatable.Updatables
import com.glyph.scala.lib.libgdx.actor.Scissor

/**
 * @author glyph
 */
class PuzzleScreen extends StagedScreen with Updatables {
  //TODO ControllerはViewのイベントをModelに渡すためのもの。
  //TODO ビューの状態遷移はビューで、ゲームの状態（ターン等）はモデルクラスでやればよい。
  val STAGE_WIDTH: Int = ScalaGame.VIRTUAL_WIDTH
  val STAGE_HEIGHT: Int = ScalaGame.VIRTUAL_HEIGHT
  /*
  init values
   */
  val game = new Game
  val root = new Table
  val gameView = new PuzzleGameView(game) with Scissor
  val gameController = new PuzzleGameController(game, gameView)
  /*
   init layout
   */
  root.setSize(STAGE_WIDTH, STAGE_HEIGHT)
  stage.addActor(root)
  root.debug()
  root.add(gameView).fill().expand()
  root.layout()
  /*
  init game
   */
  game.initialize()
  add(gameController)

  override def render(delta: Float) {
    super.render(delta)
    update(delta)
    Table.drawDebug(stage)
  }
}
