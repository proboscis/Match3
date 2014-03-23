package com.glyph._scala.lib.util.reactive

import scala.reflect.ClassTag


/**
 * @author glyph
 */
class Var[T](protected var variable: T, name: String = "undefined") extends Varying[T] {
  self =>
  def current: T = variable

  def unary_~ : self.type = self

  //debugReactive[T]("name:" + name)

  def update(f: T => T) {
    variable = f(current)
    notifyObservers(variable)
  }

  def update(v: T) {
    variable = v
    notifyObservers(variable)
  }
  def :=(v:T){
    update(v)
  }
}

object Var {
  //@specialized(Float,Int) this causes a stack overflow bug.
  //overriding specialized trait causes infinite loop!!!
  //this is fixed in scala 2.11.0-M1 and is closed, so there is no hope on fixes for 2.10.3
  //https://issues.scala-lang.org/browse/SI-4996
  //finally, i couldn't make this work............omg
  def apply[T](v: T, name: String = "undefined") = new Var(v, name)

  // how can i keep using reactive?
  // i guess this is not possible....

  //I gave up using scala collections on android
  //I gave up using reactive values in a place that is really called a lot ...
}
