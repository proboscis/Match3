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
import com.glyph.scala.game.action_puzzle.ActionPuzzle
import com.glyph.scala.game.action_puzzle.view.ActionPuzzleView

/**
 * @author glyph
 */
class ActionScreen(assets:AssetManager) extends TabledScreen with Reactor{
  val constants = RVJSON(GdxFile("constants/string.js"))
  val colors = RVJSON(GdxFile("constants/colors.js"))//RVJSON(constants.colors.asVnel[String])
  def configSrc = RVJSON(GdxFile("json/gameConfig.json"))
  //TODO ControllerはViewのイベントをModelに渡すためのもの。
  //TODO ビューの状態遷移はビューで、ゲームの状態（ターン等）はモデルクラスでやればよい。
  reactVar(colors.background.as[String].map{opt => opt.map(Color.valueOf)|Color.WHITE}){
    color => backgroundColor = color
  }

  val puzzle = new ActionPuzzle
  val view = new ActionPuzzleView(assets,puzzle)
  /*
   init layout
   */
  root.add(view).fill().expand()
  root.invalidate()
  root.layout()
  val fps = new FPSLogger

  /*
    start game
   */
  puzzle.initialize(){
    case result => println(result)
  }
}
