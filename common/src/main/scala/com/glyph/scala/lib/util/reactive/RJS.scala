package com.glyph.scala.lib.util.reactive

import com.glyph.scala.lib.util.DebugUtil
import org.mozilla.javascript.{ScriptableObject, Context}
import scala.Right
import scala.Left
import com.glyph.scala.lib.util.rhino.Rhino

/**
 * Reactive Javascript
 * @author glyph
 */
class RJS[T: Manifest](script: Varying[String], env: => Seq[(String, Any)] = Nil) extends Varying[T] with Reactor {
  var variable: T = null.asInstanceOf[T]
  reactVar(script) {
    src =>
      Rhino.evaluate[T](src,env) match {
        case Right(v) => {
          variable = v
          notifyObservers(v)
        }
        case Left(e) => e.printStackTrace()
      }
  }
  def current: T = variable
}

object RJS {
  val log = DebugUtil.log(println) _
}


