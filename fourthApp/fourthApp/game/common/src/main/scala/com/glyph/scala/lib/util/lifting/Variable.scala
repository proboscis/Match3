package com.glyph.scala.lib.util.lifting

/**
 * @author glyph
 */
trait Variable[T] {
  def update(t:T)
  def apply():T
}
