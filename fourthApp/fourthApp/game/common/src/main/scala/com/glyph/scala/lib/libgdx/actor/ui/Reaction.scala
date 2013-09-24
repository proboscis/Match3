package com.glyph.scala.lib.libgdx.actor.ui

import com.badlogic.gdx.scenes.scene2d.Action
import com.glyph.scala.lib.util.reactive.{EventSource, Varying}

/**
 * @author glyph
 */
trait Reaction extends ReactiveActor{
  reactiveValue match {
    case r:Varying[_] =>reactVar(r){_=>doReaction()}
    case r:EventSource[_] =>reactEvent(r){_=>doReaction()}
  }
  private def doReaction(){
    clearActions()
    addAction(reaction)
  }
  def reaction:Action
}
