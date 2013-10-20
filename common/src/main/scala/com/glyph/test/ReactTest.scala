package com.glyph.test

import com.glyph.scala.lib.util.reactive.{Reactor, Var}
import com.glyph.scala.lib.util.reactive
import reactive.~
/**
 * @author glyph
 */
object ReactTest extends Reactor{
  def main(args: Array[String]) {
    val a = Var("")
    val b = Var("")
    reactVar(a~b){
      case x~y => println(x,y)
    }
    a() = "a is now a"
    b() = "b is now b"
    clearReaction()
    a() = "a is now a2"
    b() = "b is now b2"
  }
}
