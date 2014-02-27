package com.glyph._scala.game.action_puzzle.view.animated

import com.badlogic.gdx.scenes.scene2d.ui.{TextButton, Skin}
import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager.AnimatedConstructor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.glyph._scala.lib.libgdx.actor.AnimatedTable

object Menu {

  import scalaz._
  import Scalaz._

  case class Style(space:Float = 20,padding:Float = 20,skin:Skin)

  val constructor: Style => AnimatedConstructor = style => info => callbacks => new AnimatedTable {
    import style._
    def label(any: Any) = new TextButton(any.toString, skin) <| (_.addListener(new ChangeListener {
      def changed(p1: ChangeEvent, p2: Actor) {
        callbacks(any.toString)(Map())
      }
    }))
    defaults().space(space).padLeft(padding).padRight(padding).fill.expand
    //TODO make this modifiable
    "Title" :: "Menu" :: "Puzzle" :: Nil map label foreach (add(_).row())
  }
}
