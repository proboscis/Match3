package com.glyph.test

import com.glyph.scala.lib.util.reactive
import reactive.Reactor

/**
 * @author glyph
 */
object RJSONTest extends Reactor {
  def main(args: Array[String]) {
    //val file = new RFile("common/src/main/resources/json/test.json")
    //val json = RJSON.apply(file)
    /*
    val x = Var(0)
    val y = Var(0)
    val text = Var("first")
    import reactive._
    //val adder:(Int,Int)=>Int ={_+_}
    val xy = (x ~ y ~ text) ->{
      case a~b~t => a + b + t
    }
    val a = (0,0)
    a match{
      case (0,0) => println("0,0")

    }
    x() match {
      case 0 =>
      case 1 =>
    }
    reactVar(xy)(println)
    x() = 2
    x() = 4
    y() = 2
    y() = 1
    */
  }
}
