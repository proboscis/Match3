package com.glyph.scala.lib.util

/**
 * @author glyph
 */
abstract trait Poolable{
  /**
   * do not call this method out side of Pool class
   */
  def free()
}
