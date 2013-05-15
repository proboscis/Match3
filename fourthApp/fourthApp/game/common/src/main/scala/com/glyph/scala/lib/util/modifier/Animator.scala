package com.glyph.scala.lib.util.modifier

import collection.mutable

/**
 * @author glyph
 */
trait Animator[T] extends Modifier[T]{
  val actions = mutable.Queue[Action[T]]()
  var current :Action[T] = null
  def act(delta: Float) {
    if (current == null){
      if(!actions.isEmpty){
        current = actions.dequeue()
      }
    }
    if (current != null){
      current(variable,delta)
      if (current.isComplete){
        current = null
      }
    }
  }

  def addAction(action:Action[T]){
    actions += action
  }
}
