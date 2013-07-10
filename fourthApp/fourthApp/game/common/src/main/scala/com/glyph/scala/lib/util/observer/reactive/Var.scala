package com.glyph.scala.lib.util.observer.reactive


/**
 * @author glyph
 */
class Var[T](private var variable: T) extends Varying[T] {
  self =>

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
  def apply[T](v: T) = new Var(v)
}
