package com.glyph.scala.game.action_puzzle.screen

import com.glyph.scala.lib.libgdx.screen.ConfiguredScreen
import com.glyph.scala.lib.util.json.RVJSON
import com.glyph.scala.lib.libgdx.reactive.GdxFile
import com.badlogic.gdx.graphics._
import com.glyph.scala.lib.util.reactive.{Var, Reactor}
import scalaz._
import Scalaz._
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.game.action_puzzle.{APView, ActionPuzzle}
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.glyph.scala.lib.util.Logging
import com.badlogic.gdx.math.{Interpolation, MathUtils}
import com.glyph.scala.lib.libgdx.actor.ui.RLabel
import com.glyph.scala.lib.util.updatable.reactive.Eased
import com.glyph.scala.lib.libgdx.actor.Updating
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions

/**
 * @author glyph
 */
class ActionScreen(implicit assets: AssetManager) extends ConfiguredScreen with Reactor with Logging {
  val constants = RVJSON(GdxFile("constants/string.js"))
  val colors = RVJSON(GdxFile("constants/colors.js"))
  //RVJSON(constants.colors.asVnel[String])
  //TODO ControllerはViewのイベントをModelに渡すためのもの。
  //TODO ビューの状態遷移はビューで、ゲームの状態（ターン等）はモデルクラスでやればよい。
  val bgColor = colors.background.as[String] map (_.map(Color.valueOf) | Color.WHITE)
  reactVar(bgColor)(backgroundColor = _)
  val skin = assets.get[Skin]("skin/default.json")
  val puzzle = new ActionPuzzle(6, 6, () => MathUtils.random(0, 3), (a: Int, b: Int) => {
    a == b
  })
  val score = Var(0f)
  val easedScore = Eased(score, Interpolation.exp10Out.apply, _ / 10f)
  val view = new APView(puzzle, assets) with Updating
  view.add(easedScore)


  //val view = new APView2(puzzle)
  /*
   init layout
   */
  view.setSize(STAGE_WIDTH, STAGE_WIDTH)
  //this is required
  val scoreLabel = new RLabel(skin, easedScore.map("%.0f".format(_)))
  scoreLabel.setColor(Color.DARK_GRAY)
  scoreLabel.setFontScale(2f)
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
  root.add(scoreLabel).height((STAGE_HEIGHT - STAGE_WIDTH) / 2).fill.expand.row
  root.add(view).fill().expand().width(STAGE_WIDTH).height(STAGE_WIDTH)
  root.invalidate()
  root.layout()
  puzzle.panelAdd = view.panelAdd
  puzzle.panelRemove = seq => {
    score() += 10 * seq.size
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
    puzzle.update(delta + (score()/1000f/1000f))
  }
}
