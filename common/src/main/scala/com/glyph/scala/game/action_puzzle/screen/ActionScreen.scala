package com.glyph.scala.game.action_puzzle.screen

import com.glyph.scala.lib.libgdx.screen.TabledScreen
import com.glyph.scala.lib.util.json.{JSON, RVJSON}
import com.glyph.scala.lib.libgdx.reactive.GdxFile
import com.badlogic.gdx.graphics.{FPSLogger, Color}
import com.glyph.scala.game.puzzle.model.Game
import com.glyph.scala.game.puzzle.controller.PuzzleGameController
import com.glyph.scala.game.puzzle.view.PuzzleGameView
import com.glyph.scala.lib.libgdx.actor.Scissor
import com.glyph.scala.lib.util.reactive.{Reactor, Varying}
import scalaz._
import Scalaz._
import com.badlogic.gdx.assets.AssetManager

/**
 * @author glyph
 */
class ActionScreen(assets:AssetManager) extends TabledScreen with Reactor{
  val constants = RVJSON(GdxFile("constants/string.js"))
  val colors = RVJSON(constants.colors.asVnel[String])
  def configSrc = RVJSON(GdxFile("json/gameConfig.json"))
  //TODO ControllerはViewのイベントをModelに渡すためのもの。
  //TODO ビューの状態遷移はビューで、ゲームの状態（ターン等）はモデルクラスでやればよい。
  reactVar(colors.background.as[String].map{opt => opt.map(Color.valueOf)|Color.WHITE}){
    color => backgroundColor = color
  }

  //Model
  val game = new Game(GdxFile(_))
  //Controller
  val gameController = new PuzzleGameController(game)
  //TODO make View Controller
  //View
  val gameView = new PuzzleGameView(assets,game, gameController.deck, (STAGE_WIDTH, STAGE_HEIGHT)) with Scissor
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
