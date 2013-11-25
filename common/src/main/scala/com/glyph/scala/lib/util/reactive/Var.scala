package com.glyph.scala.lib.util.reactive

import ref.WeakReference
import com.glyph.scala.lib.util.lifting.Variable


/**
 * @author glyph
 */
class Var[T: Manifest](protected var variable: T, name: String = "undefined") extends Varying[T] {
  self =>
  def current: T = variable

  def unary_~ : self.type = self

  debugReactive[T]("name:" + name)

  def update(f: T => T) {
    notifyObservers(f(current))
  }

  def update(v: T) {
    notifyObservers(v)
  }

  override def notifyObservers(t: T) {
    variable = t
    super.notifyObservers(t)
  }
}

object Var {
  def apply[T: Manifest](v: T, name: String = "undefined") = new Var(v, name)
}
