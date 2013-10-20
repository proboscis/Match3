package com.glyph.test

/**
 * @author glyph
 */
object CaseTest {

  case class A()


  def main(args: Array[String]) {
    val a :Any= A()
    a match{
      case A => println("object")
      case A() => println("instance")
    }
  }
}
