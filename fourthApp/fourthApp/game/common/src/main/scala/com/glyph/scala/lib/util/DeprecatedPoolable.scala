package com.glyph.scala.lib.util

/**
 * @author glyph
 */
@deprecated
abstract trait DeprecatedPoolable{
  /**
   * do not call this method out side of Pool class
   */
  def free()
}
