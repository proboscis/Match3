package com.glyph._scala.game.action_puzzle.view.animated

import com.badlogic.gdx.scenes.scene2d.ui.{TextButton, Skin}
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager.AnimatedConstructor
import com.glyph._scala.lib.libgdx.actor.AnimatedTable
import com.glyph._scala.lib.util.updatable.reactive.Eased
import com.glyph._scala.lib.util.updatable.Updatables
import com.glyph._scala.lib.util.reactive.Var
import com.badlogic.gdx.math.Interpolation
import com.glyph._scala.lib.libgdx.actor.ui.RLabel
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.glyph._scala.test.{MockTransition, AnimatedRunner}
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager
import com.badlogic.gdx.assets.AssetManager
import com.glyph._scala.game.builders.{AnimatedConstructors, Builders}

/**
 * @author glyph
 */
object GameResult {
  val constructor :Skin => AnimatedConstructor = skin => info => callbacks => new AnimatedTable
    with Updatables{
    val score = info("score").asInstanceOf[Int]
    val shownScore = Var(0f)
    val ease = Eased(shownScore,Interpolation.linear.apply,t=>2f)
    val scoreLabel = new RLabel(skin,ease.map(s => "%.0f".format(s)))
    val replayButton = new TextButton("Replay",skin) with Change
    val titleButton = new TextButton("Back to Title",skin) with Change
    replayButton.onChange =(e,a)=>callbacks("replay")(Map())
    titleButton.onChange = (e,a)=>callbacks("title")(Map())
    add(ease)
    add(scoreLabel).center.fill.expand.row
    add(replayButton).fill().expand().row()
    add(titleButton).fill().expand().row()
    shownScore ()= score
  }
}

trait Change{
  self:Actor =>
  var onChange = (event:ChangeEvent,actor:Actor)=>{}
  self.addListener(new ChangeListener {
    def changed(event:ChangeEvent,actor:Actor){
      onChange(event,actor)
    }
  })
}
class GameResultTest extends MockTransition{
  override implicit def assetManager: AssetManager = new AssetManager()
  manager.start(result,Map("score"->1000),holder.push)
}