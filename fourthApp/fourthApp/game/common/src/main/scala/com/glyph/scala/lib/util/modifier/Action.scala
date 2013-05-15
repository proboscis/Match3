package com.glyph.scala.lib.util.modifier

/**
 * @author glyph
 */
trait Action[T] {
  def apply(input:T,delta:Float)
  def isComplete:Boolean
}
