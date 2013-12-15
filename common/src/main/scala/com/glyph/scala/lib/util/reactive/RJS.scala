package com.glyph.scala.lib.util.reactive

import com.glyph.scala.lib.util.rhino.Rhino
import scalaz._
import Scalaz._

/**
 * Reactive Javascript
 * @author glyph
 */
class RJS[T: Manifest](script: Varying[ValidationNel[Throwable, String]], env: => Seq[(String, Any)] = Nil) extends Varying[T] with Reactor {
  var variable: T = null.asInstanceOf[T]
  reactVar(script) {
    _.flatMap {
      src => Rhino.evaluate[T](src, env).fold(_.failNel, _.success)
    } match {
      case Success(v) => {
        variable = v
        notifyObservers(v)
      }
      case Failure(e) => e.foreach(_.printStackTrace())
    }
  }

  def current: T = variable
}