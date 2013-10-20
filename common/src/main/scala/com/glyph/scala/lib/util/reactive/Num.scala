package com.glyph.scala.lib.util.reactive

/**
 * @author glyph
 */
class Num[T:Numeric:Manifest](initial:T,name:String = "undefined Num") extends Var[T](initial,name){
  private val evT = implicitly[Numeric[T]]
}
