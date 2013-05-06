package com.glyph.scala.lib.util.observer

import collection.mutable.ListBuffer

/**
 * @author glyph
 */
trait Observable[T]{
  this:T=>
  type Callback = (T , Any) => Unit
  val observers = ListBuffer.empty[Callback]

  def register(f: Callback) {
    observers += f
  }

  def unregister(f: Callback) {
    observers -= f
  }

  protected def notifyObservers() {
    observers.foreach {
      f => f(this, null)
    }
  }

  protected def notifyObservers(value: Any) {
    observers.foreach {
      f => f(this, value)
    }
  }
}
