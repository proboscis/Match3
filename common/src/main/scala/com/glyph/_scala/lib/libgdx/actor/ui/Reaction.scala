package com.glyph._scala.lib.libgdx.actor.ui

import com.badlogic.gdx.scenes.scene2d.Action
import com.glyph._scala.lib.util.reactive.{EventSource, Varying}

/**
 * @author glyph
 */
trait Reaction[T]{
  self:ReactiveActor[T] =>
  private var initialX = 0f
  private var initialY = 0f
  reactiveValue match {
    case r:Varying[T] =>reactEvent(r.toEvents){_=>doReaction()}
    case r:EventSource[T] =>reactEvent(r){_=>doReaction()}
  }
  private def doReaction(){
    if(getActions.size != 0){
      setPosition(initialX,initialY)
      initialX = getX
      initialY = getY
      clearActions()
    }
    addAction(reaction)
  }
  def reaction:Action
}
