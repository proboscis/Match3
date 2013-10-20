package com.glyph.test


/**
 * @author glyph
 */
object CovariantTest {
  def main (args: Array[String]) {
    class A
    class B extends A
    val fa: (A)=>Unit = a=>{println("a")}
    val fb: (B)=>Unit = b=>{println("b")}
    val test:(B)=>Unit = fa
  }
}
