package com.glyph.test

import com.glyph.scala.lib.util.reactive.{Reactor, RFile}
import com.glyph.scala.lib.util.json.{RJSON, JSON}

/**
 * @author glyph
 */
object JSFunctionTest extends Reactor{
  def main(args: Array[String]) {
    val script = RJSON(new RFile("common/src/main/resources/js/function.js").map{_|""})
    reactVar(script.hello.asFunction){
      e => for(f <- e){
        println("apply function")
        f(0)
      }
    }
  }
}
