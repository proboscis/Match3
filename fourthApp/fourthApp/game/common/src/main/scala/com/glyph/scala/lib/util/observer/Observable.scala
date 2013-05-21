package com.glyph.scala.lib.util.observer
import com.glyph.scala.lib.util.collection.LinkedList

/**
 * @author glyph
 */
trait Observable[T]{
  this:T=>
  type Callback = (T , Any) => Unit
  val observers = new LinkedList[Callback]

  def register(f: Callback) {
    observers push f
  }

  def unregister(f: Callback) {
    observers remove f
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
