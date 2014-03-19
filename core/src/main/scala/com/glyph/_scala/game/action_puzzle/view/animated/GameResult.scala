package com.glyph._scala.game.action_puzzle.view.animated

import com.badlogic.gdx.scenes.scene2d.ui.{Label, Table, TextButton, Skin}
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager.AnimatedConstructor
import com.glyph._scala.lib.libgdx.actor.AnimatedTable
import com.glyph._scala.lib.util.updatable.reactive.Eased
import com.glyph._scala.lib.util.updatable.Updatables
import com.glyph._scala.lib.util.reactive.{VClass, Reactor, Var}
import com.badlogic.gdx.math.{MathUtils, Interpolation}
import com.glyph._scala.lib.libgdx.actor.ui.RLabel
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.glyph._scala.test.MockTransition
import com.badlogic.gdx.assets.AssetManager
import com.esotericsoftware.tablelayout.{Value, BaseTableLayout}
import com.glyph._scala.lib.libgdx.actor.widgets.Center
import com.glyph._scala.social.SocialManager
import com.glyph._scala.game.action_puzzle.{ComboPuzzle, LocalLeaderBoard}
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager
import com.glyph._scala.lib.util.Animated
import GameResult._
import com.glyph._scala.lib.util.updatable.task.ParallelProcessor

/**
 * @author glyph
 */
class GameResult(style:Style)(implicit processor:ParallelProcessor) extends AnimatedConstructor{
  override def apply(info: AnimatedManager.Info): (AnimatedManager.Callbacks) => Actor with Animated = callbacks =>  new AnimatedTable
    with Updatables
    with Reactor {
    import style._
    debug(BaseTableLayout.Debug.all)
    log("creating game result view")
    val score = info.lift("score").getOrElse(0).asInstanceOf[Int]
    val shownScore = Var(MathUtils.random(0f))
    val ease = Eased(shownScore, Interpolation.exp10Out.apply, t => 2f)
    //if you wanna center the elements, use inner table!
    val scoreLabel = Center(new RLabel(skin, ease.map(s => "%.0f".format(s)))).left()
    val replayButton = new TextButton("Replay", skin) with Change
    val titleButton = new TextButton("Back to Title", skin) with Change
    val dashBoardButton = new TextButton("Dash Board",skin) with Change
    replayButton.onChange = (e, a) => callbacks("replay")(Map())
    titleButton.onChange = (e, a) => callbacks("title")(Map())
    dashBoardButton.onChange = (e,a) => SocialManager.manager.showGlobalHighScore()
    add(ease)
    shownScore() = score
    defaults().space(space).padLeft(style.pad).padRight(style.pad).fill.expandX.height(160)
    setSkin(skin)

    val scoreTable = new Table()
    scoreTable.add(Center(new Label("Score: ",skin)).right()).width(Value.percentWidth(0.5f))
    scoreTable.add(scoreLabel).fill.expand
    add(scoreTable).height(Value.percentHeight(0.7f)).row()
    add(replayButton).row()
    add(dashBoardButton).row()
    add(titleButton).row()
    log("highscore:==============>")
    LocalLeaderBoard.load(ComboPuzzle.LOCAL_LEADERBOARD).foreach(log)
    log("highscore:<==============")
  }

}
object GameResult {
  case class Style(pad:Float = 20, space:Float = 20,skin:Skin)
}
trait Change {
  self: Actor =>
  var onChange = (event: ChangeEvent, actor: Actor) => {}
  self.addListener(new ChangeListener {
    def changed(event: ChangeEvent, actor: Actor) {
      onChange(event, actor)
    }
  })
}




