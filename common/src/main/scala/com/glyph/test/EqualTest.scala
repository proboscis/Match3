package com.glyph.test

import com.glyph.scala.lib.util.observer.{Observable, Observing}

/**
 * @author glyph
 */
object EqualTest extends Observing{
  def main(args: Array[String]) {
    val value = new Observable[Int]
    val o1 = observe(value){_=>}
    val o2 = observe(value){_=>}
    println(o1 == o2)
    println(o1 eq o2)
  }
}
