package com.glyph.scala.lib.util.laz/**
 * @author glyph
 */
trait LazyValue[T]{
  def initialize(value:T):Boolean
}
