package com.glyph.scala.game.action_puzzle.screen

import com.glyph.scala.lib.libgdx.screen.TabledScreen
import com.glyph.scala.lib.util.json.RVJSON
import com.glyph.scala.lib.libgdx.reactive.GdxFile
import com.badlogic.gdx.graphics._
import com.glyph.scala.lib.util.reactive.Reactor
import scalaz._
import Scalaz._
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.game.action_puzzle.{APView, ActionPuzzle3}
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.glyph.scala.lib.util.{reactive, Logging}

/**
 * @author glyph
 */
class ActionScreen(assets: AssetManager) extends TabledScreen with Reactor with Logging {
  val constants = RVJSON(GdxFile("constants/string.js"))
  val colors = RVJSON(GdxFile("constants/colors.js"))

  //RVJSON(constants.colors.asVnel[String])
  def configSrc = RVJSON(GdxFile("json/gameConfig.json"))

  //TODO ControllerはViewのイベントをModelに渡すためのもの。
  //TODO ビューの状態遷移はビューで、ゲームの状態（ターン等）はモデルクラスでやればよい。

  reactVar(colors.background.as[String].map {
    opt => opt.map(Color.valueOf) | Color.WHITE
  }) {
    color => backgroundColor = color
  }

  val skin = assets.get[Skin]("skin/default.json")
  val puzzle = new ActionPuzzle3
  val view = new APView(puzzle, assets)
  /*
   init layout
   */
  view.setSize(STAGE_WIDTH,STAGE_WIDTH)//this is required
  root.add(view).fill().expand().width(STAGE_WIDTH).height(STAGE_WIDTH)
  root.invalidate()
  root.layout()
/*
    import reactive._
    import puzzle._
    reactVar(fixed~falling~future~swiping){
      case a~b~c~d=> "====================="::(a::b::c::Nil map(_.text))::d::Nil foreach log
    }
*/
  puzzle.panelAdd = view.panelAdd
  puzzle.panelRemove = view.panelRemove
  view.startSwipeCheck(puzzle.pooledSwipe)
  /*
  init after the layout is setup
   */
  puzzle.initialize()

  override def render(delta: Float): Unit = {
    super.render(delta)
    puzzle.update(delta)
  }
}
