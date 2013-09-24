package com.glyph.test

/**
 * @author glyph
 */
object BoundTest {

  def test[T:IBDS]{
    println (implicitly[IBDS[T]] match {
      case I => "Int"
      case B => "bool"
      case D => "double"
      case S => "String"
    })
  }
  def main(args: Array[String]) {
    test[Double]
  }
  sealed class IBDS[T]
  implicit object I extends IBDS[Int]
  implicit object B extends IBDS[Boolean]
  implicit object D extends IBDS[Double]
  implicit object S extends IBDS[String]
}
