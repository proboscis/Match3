package com.glyph.scala.game.action_puzzle.screen

import com.glyph.scala.lib.libgdx.screen.{ScreenBuilder, ConfiguredScreen}
import com.glyph.scala.lib.util.json.RVJSON
import com.glyph.scala.lib.libgdx.reactive.GdxFile
import com.badlogic.gdx.graphics._
import com.glyph.scala.lib.util.reactive.{Var, Reactor}
import scalaz._
import Scalaz._
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.game.action_puzzle.{ComboPuzzle, APView, ActionPuzzle}
import com.badlogic.gdx.scenes.scene2d.ui.{Table, Skin}
import com.glyph.scala.lib.util.Logging
import com.badlogic.gdx.math.{Interpolation, MathUtils}
import com.glyph.scala.lib.libgdx.actor.ui.RLabel
import com.glyph.scala.lib.util.updatable.reactive.Eased
import com.glyph.scala.lib.libgdx.actor.{SpriteActor, Updating}
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.graphics.g2d.{BitmapFont, Sprite}
import com.badlogic.gdx.Screen
import com.glyph.scala.lib.libgdx.font.FontUtil
import com.glyph.scala.lib.libgdx.gl.ShaderHandler

/**
 * @author glyph
 */
class ActionScreen(implicit assets: AssetManager) extends ConfiguredScreen with Reactor with Logging {
  //TODO design the gauge
  //TODO GameOver
  //TODO title screen
  //TODO loading screen
  val constants = RVJSON(GdxFile("constants/string.js"))
  val colors = RVJSON(GdxFile("constants/colors.js"))
  autoClearScreen = false
  //RVJSON(constants.colors.asVnel[String])
  //TODO ControllerはViewのイベントをModelに渡すためのもの。
  //TODO ビューの状態遷移はビューで、ゲームの状態（ターン等）はモデルクラスでやればよい。
  val bgColor = colors.background.as[String] map (_.map(Color.valueOf) | Color.WHITE)
  reactVar(bgColor)(backgroundColor = _)
  val skin = assets.get[Skin]("skin/holo/Holo-dark-xhdpi.json")
  val game = new ComboPuzzle
  import game._
  val easedScore = Eased(score map (_.toFloat), Interpolation.exp10Out.apply, _ / 10f)
  val view = new APView(score,puzzle, assets) with Updating

  //TODO uncomment this to enable easing score
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
      prevAction = sequence(color(Color.WHITE, 0.5f,exp10Out), color(ic, 1f,exp10In))
      scoreLabel.addAction(prevAction)
    }
  }
  val comboLabel = new RLabel(skin,combo map(_.toString))
  val inner = new Table()
  inner.debug
  inner.add(scoreLabel).expand
  inner.add(comboLabel).expand
  root.add(inner).fill.expand.row
  root.add(view).fill().expand().width(STAGE_WIDTH).height(STAGE_WIDTH).left.row
  root.add(new Table{
    val back = SpriteActor(new Sprite(assets.get[Texture]("data/dummy.png")))
    add(back).fill.expand
    reactVar(time map (_/60f * STAGE_WIDTH))(back.setWidth)
  }).size(STAGE_WIDTH,40).fill().expand()
  root.invalidate()
  root.layout()
  game.onPanelAdd = view.panelAdd
  game.onPanelRemove = seq => {
    view.panelRemove(seq)
  }
  reactSome(view.swipeChecker){
    case checker => {
      view.swipeStopper()
      checker(puzzle.pooledSwipe)
      //view.startSwipeCheck(puzzle.pooledSwipe)
    }
  }
  root.debug()
  /*
  init after the layout is setup
   */
  puzzle.initialize()

  override def render(delta: Float): Unit = {
    clearScreen()

    super.render(delta)
    game.update(delta)
  }
}

class ActionPuzzleScreen extends ScreenBuilder{
  //TODO serialization is the hardest thing to do in android, you know
  def requiredAssets: Set[(Class[_], Seq[String])] = Set(
    classOf[Texture]->Seq("data/dummy.png", "data/particle.png", "data/sword.png", "data/round_rect.png"),
    classOf[Skin]->Seq("skin/holo/Holo-dark-xhdpi.json")
  )
  def create(assetManager: AssetManager): Screen = {
    implicit val _ = assetManager
    new ActionScreen
  }
}