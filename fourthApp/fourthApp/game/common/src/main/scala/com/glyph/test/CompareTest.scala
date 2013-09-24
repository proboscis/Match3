package com.glyph.test

/**
 * @author glyph
 */
object CompareTest {
  def main(args: Array[String]) {
    val a = List(1,2,List(1,1))
    val b = List(1,2,Vector(1,1))
    println(a == b)
  }
}
