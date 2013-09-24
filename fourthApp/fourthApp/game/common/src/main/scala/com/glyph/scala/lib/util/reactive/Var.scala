package com.glyph.scala.lib.util.reactive

import ref.WeakReference
import com.glyph.scala.lib.util.lifting.Variable


/**
 * @author glyph
 */
class Var[T](protected var variable: T) extends Varying[T] with Variable[T] {
  self =>
  Var.allVariables = WeakReference(this)::Var.allVariables
  def current: T = variable
  def unary_~ :self.type = self
  def update(v: T) {
    notifyObservers(v)
  }
  override def notifyObservers(t: T) {
    variable = t
     super.notifyObservers(t)
  }
}
object Var {
  var allVariables :List[WeakReference[Var[_]]] = Nil
  def apply[T](v: T) = new Var(v)
}
