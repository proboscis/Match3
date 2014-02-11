package com.glyph._scala.game.action_puzzle.view.animated

import com.badlogic.gdx.scenes.scene2d.ui.{Table, TextButton, Skin}
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager.AnimatedConstructor
import com.glyph._scala.lib.libgdx.actor.AnimatedTable
import com.glyph._scala.lib.util.updatable.reactive.Eased
import com.glyph._scala.lib.util.updatable.Updatables
import com.glyph._scala.lib.util.reactive.{Reactor, Var}
import com.badlogic.gdx.math.Interpolation
import com.glyph._scala.lib.libgdx.actor.ui.RLabel
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.glyph._scala.test.MockTransition
import com.badlogic.gdx.assets.AssetManager
import com.esotericsoftware.tablelayout.BaseTableLayout
import com.glyph._scala.lib.libgdx.actor.widgets.Center

/**
 * @author glyph
 */
object GameResult {
  val constructor: Skin => AnimatedConstructor = skin => info => callbacks => new AnimatedTable
    with Updatables
    with Reactor {
    debug(BaseTableLayout.Debug.all)
    log("creating game result view")
    val score = info("score").asInstanceOf[Int]
    val shownScore = Var(0f)
    val ease = Eased(shownScore, Interpolation.exp10Out.apply, t => 2f)
    //if you wanna center the elements, use inner table!
    val scoreLabel = Center(new RLabel(skin, ease.map(s => "%.0f".format(s))))
    val replayButton = new TextButton("Replay", skin) with Change
    val titleButton = new TextButton("Back to Title", skin) with Change
    replayButton.onChange = (e, a) => callbacks("replay")(Map())
    titleButton.onChange = (e, a) => callbacks("title")(Map())
    add(ease)
    shownScore() = score
    defaults().space(20).padLeft(20).padRight(20).fill.expand
    add(scoreLabel).row()
    add(replayButton).row()
    add(titleButton).row()
  }
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

class GameResultTest extends MockTransition {
  override implicit def assetManager: AssetManager = new AssetManager()

  manager.start(result, Map("score" -> 1000), holder.push)
}