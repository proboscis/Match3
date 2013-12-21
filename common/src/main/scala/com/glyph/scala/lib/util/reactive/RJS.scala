package com.glyph.scala.lib.util.reactive

import com.glyph.scala.lib.util.rhino.Rhino
import scala.util.Try

/**
 * Reactive Javascript
 * @author glyph
 */
class RJS[T: Manifest](script: Varying[Try[String]], env: => Seq[(String, Any)] = Nil) extends Varying[T] with Reactor {
  var variable: T = null.asInstanceOf[T]
  reactVar(script) {
    _.flatMap {
      src => Rhino.evaluate[T](src, env)
    } match {
      case util.Success(v) => {
        variable = v
        notifyObservers(v)
      }
      case util.Failure(e) => e.printStackTrace()
    }
  }

  def current: T = variable
}