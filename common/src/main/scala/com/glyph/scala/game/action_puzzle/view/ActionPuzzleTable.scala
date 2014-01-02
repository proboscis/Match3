package com.glyph.scala.game.action_puzzle.view

import com.glyph.scala.lib.util.json.RVJSON
import com.glyph.scala.lib.libgdx.reactive.GdxFile
import com.badlogic.gdx.graphics.{Texture, Color}
import com.badlogic.gdx.scenes.scene2d.ui.{Table, Skin}
import com.glyph.scala.game.action_puzzle.{APView, ComboPuzzle}
import com.glyph.scala.lib.util.updatable.reactive.Eased
import com.badlogic.gdx.math.Interpolation
import com.glyph.scala.lib.libgdx.actor.{SpriteActor, Updating}
import com.glyph.scala.lib.libgdx.actor.ui.RLabel
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.graphics.g2d.Sprite
import com.glyph.scala.lib.util.reactive.Reactor
import com.glyph.scala.lib.util.Logging
import com.badlogic.gdx.assets.AssetManager
import scalaz._
import Scalaz._
import com.glyph.scala.lib.libgdx.screen.{ConfiguredScreen, ScreenBuilder}
import com.badlogic.gdx.Screen

/**
 * @author glyph
 */
class ActionPuzzleTable(assets: AssetManager, STAGE_WIDTH: Int, STAGE_HEIGHT: Int) extends Table with Reactor with Logging {
  //TODO design the gauge
  //TODO GameOver
  //TODO title screen
  //TODO loading screen

  log("new ActionScreen")
  val constants = RVJSON(GdxFile("constants/string.js"))
  val colors = RVJSON(GdxFile("constants/colors.js"))
  //RVJSON(constants.colors.asVnel[String])
  //TODO ControllerはViewのイベントをModelに渡すためのもの。
  //TODO ビューの状態遷移はビューで、ゲームの状態（ターン等）はモデルクラスでやればよい。
  val bgColor = colors.background.as[String] map (_.map(Color.valueOf) | Color.WHITE)
  val skin = assets.get[Skin]("skin/holo/Holo-dark-xhdpi.json")
  val game = new ComboPuzzle

  import game._

  val easedScore = Eased(score map (_.toFloat), Interpolation.exp10Out.apply, _ / 10f)
  val view = new APView(score, puzzle, assets) with Updating
  view.add(easedScore)


  //val view = new APView2(puzzle)
  /*
   init layout
   */
  view.setSize(STAGE_WIDTH, STAGE_WIDTH)
  //this is required
  val scoreLabel = new RLabel(skin, easedScore.map("%.0f".format(_)))
  scoreLabel.setColor(Color.DARK_GRAY)

  /**
   * what to do
   * react variable
   * cancel previous effect
   * add new effect
   */

  import Actions._
  import Interpolation._

  reactVar(score) {
    val ic = scoreLabel.getColor.cpy()
    var prevAction: Action = null
    s => {
      if (prevAction != null) {
        scoreLabel.removeAction(prevAction)
      }
      prevAction = sequence(color(Color.WHITE, 0.5f, exp10Out), color(ic, 1f, exp10In))
      scoreLabel.addAction(prevAction)
    }
  }
  val comboLabel = new RLabel(skin, combo map (_.toString))
  val inner = new Table()
  inner.debug
  inner.add(scoreLabel).expand
  inner.add(comboLabel).expand
  this.add(inner).fill.expand.row
  this.add(view).fill().expand().width(STAGE_WIDTH).height(STAGE_WIDTH).left.row
  this.add(new Table {
    val back = SpriteActor(new Sprite(assets.get[Texture]("data/dummy.png")))
    add(back).fill.expand
    time map (_ / 60f * STAGE_WIDTH) += back.setWidth
  }).size(STAGE_WIDTH, 40).fill().expand()
  this.invalidate()
  this.layout()
  game.onPanelAdd = view.panelAdd
  game.onPanelRemove = seq => {
    view.panelRemove(seq)
  }
  reactSome(view.swipeChecker) {
    case checker => {
      view.swipeStopper()
      checker(puzzle.pooledSwipe)
      //view.startSwipeCheck(puzzle.pooledSwipe)
    }
  }
  this.debug()
  /*
  init after the layout is setup
   */
  puzzle.initialize()

  override def act(delta: Float): Unit = {
    game.update(delta)
    super.act(delta)
  }
}
object ActionPuzzleTable{
  val requiredAssets:Set[(Class[_], Seq[String])] = Set(
    classOf[Texture]->Seq("data/dummy.png", "data/particle.png", "data/sword.png", "data/round_rect.png"),
    classOf[Skin]->Seq("skin/holo/Holo-dark-xhdpi.json")
  )
}
class ActionPuzzleTableScreen extends ScreenBuilder{
  def requiredAssets = ActionPuzzleTable.requiredAssets

  def create(assetManager: AssetManager): Screen = new ConfiguredScreen{
    val table = new ActionPuzzleTable(assetManager,STAGE_WIDTH/2,STAGE_HEIGHT/2)
    root.add(table).size(STAGE_WIDTH/2,STAGE_HEIGHT/2)
    root.debug()
  }
}