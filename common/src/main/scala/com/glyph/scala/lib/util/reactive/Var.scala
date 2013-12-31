package com.glyph.scala.lib.util.reactive

import ref.WeakReference
import com.glyph.scala.lib.util.lifting.Variable


/**
 * @author glyph
 */
class Var[@specialized(Float,Int)T: Manifest](protected var variable: T, name: String = "undefined") extends Varying[T] {
  self =>
  def current: T = variable

  def unary_~ : self.type = self

  debugReactive[T]("name:" + name)

  def update(f: T => T) {
    variable = f(current)
    notifyObservers(variable)
  }

  def update(v: T) {
    variable = v
    notifyObservers(variable)
  }
}

object Var {
  //@specialized(Float,Int) this causes a stack overflow bug.
  //overriding specialized trait causes infinite loop!!!
  //this is fixed in scala 2.11.0-M1 and is closed, so there is no hope on fixes for 2.10.3
  //https://issues.scala-lang.org/browse/SI-4996
  def apply[@specialized(Float,Int)T: Manifest](v: T, name: String = "undefined") = new Var(v, name)
}
