package com.glyph.scala.lib.util

/**
 * @author glyph
 */
trait Animated {
  def in(cb: () => Unit)

  def out(cb: () => Unit)

  def pause(cb: () => Unit)

  def resume(cb: () => Unit)
}
