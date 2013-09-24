package com.glyph.scala.lib.util.reactive

/**
 * @author glyph
 */
class Num[T:Numeric](initial:T) extends Var(initial){
  private val evT = implicitly[Numeric[T]]
}
