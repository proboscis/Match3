package com.glyph.scala.game.action_puzzle.screen

import com.glyph.scala.lib.libgdx.screen.TabledScreen
import com.glyph.scala.lib.util.json.RVJSON
import com.glyph.scala.lib.libgdx.reactive.GdxFile
import com.badlogic.gdx.graphics._
import com.glyph.scala.lib.util.reactive.{Var, Reactor}
import scalaz._
import Scalaz._
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.game.action_puzzle.{ GMatch3, APView, ActionPuzzle}
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.glyph.scala.lib.util.{reactive, Logging}
import com.badlogic.gdx.math.MathUtils
import com.glyph.scala.lib.libgdx.actor.ui.{RLabel, Gauge}

/**
 * @author glyph
 */
class ActionScreen(implicit assets: AssetManager) extends TabledScreen with Reactor with Logging {
  val constants = RVJSON(GdxFile("constants/string.js"))
  val colors = RVJSON(GdxFile("constants/colors.js"))

  //RVJSON(constants.colors.asVnel[String])
  def configSrc = RVJSON(GdxFile("json/gameConfig.json"))

  //TODO ControllerはViewのイベントをModelに渡すためのもの。
  //TODO ビューの状態遷移はビューで、ゲームの状態（ターン等）はモデルクラスでやればよい。

  val bgColor = colors.background.as[String] map (_.map(Color.valueOf) | Color.WHITE)
  reactVar(bgColor)(backgroundColor = _)

  val skin = assets.get[Skin]("skin/default.json")
  val puzzle = new ActionPuzzle(6,6,()=>MathUtils.random(0,3),(a:Int,b:Int)=>{a == b})
  val score = Var(0f)
  val view = new APView(puzzle, assets)
  //val view = new APView2(puzzle)
  /*
   init layout
   */
  view.setSize(STAGE_WIDTH, STAGE_WIDTH) //this is required
  val scoreLabel = new RLabel(skin,score.map(_+""))
  root.add(scoreLabel).height((STAGE_HEIGHT-STAGE_WIDTH)/2).fill.expand.row
  root.add(view).fill().expand().width(STAGE_WIDTH).height(STAGE_WIDTH)
  root.invalidate()
  root.layout()
  puzzle.panelAdd = view.panelAdd
  puzzle.panelRemove = seq => {

    view.panelRemove(seq)
  }
  view.startSwipeCheck(puzzle.pooledSwipe)
  root.debug()
  /*
  init after the layout is setup
   */
  puzzle.initialize()

  override def render(delta: Float): Unit = {
    super.render(delta)
    puzzle.update(delta)
  }
}
