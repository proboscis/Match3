package com.glyph.ruby

import com.glyph.scala.lib.util.reactive.{Reactor, Varying}
import org.jruby.runtime.builtin.IRubyObject
import org.jruby.javasupport.JavaEmbedUtils
import java.util
import com.glyph.scala.lib.util.DebugUtil

/**
 * @author glyph
 */
class RRuby(script: Varying[String]) extends Varying[IRubyObject] with Reactor {
  var variable: IRubyObject = null

  import scala.util.control.Exception._
  import RRuby._

  val log = DebugUtil.log(println) _
  reactVar(script) {
    s => log("reload ruby") {
      allCatch either evaluator.eval(runtime, s) match {
        case Right(f) => variable = f; notifyObservers(f)
        case Left(e) => e.printStackTrace()
      }
    }
  }

  def current: IRubyObject = variable
}

object RRuby {
  val runtime = JavaEmbedUtils.initialize(new util.ArrayList[Nothing]())
  val evaluator = JavaEmbedUtils.newRuntimeAdapter()
}
