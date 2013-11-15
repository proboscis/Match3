package com.glyph.scala.lib.puzzle

import scala.reflect.ClassTag
import scalaz._
import Scalaz._
/**
 * @author glyph
 */
object ArrayMatch {
  type Puzzle[T] = Array[Array[T]]
  trait Matchable[T]{
    def check(a:T,b:T):Boolean
  }
  //def initialize[T:ClassTag](width:Int):Array[Array[T]] = Array((0 until width).toList.as(Array[T]()))
}
