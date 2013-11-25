package com.glyph.scala.lib.util.animator

trait Animating[T] extends Any {
  def get: T

  def set(t: T)
}


