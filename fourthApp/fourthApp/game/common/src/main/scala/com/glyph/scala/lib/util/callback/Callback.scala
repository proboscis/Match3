package com.glyph.scala.lib.util.callback

/**
 * @author glyph
 */
class Callback {
  private var function = ()=>{}
  def apply(){function()}
  def apply(f: => Unit){
    function = ()=>f
  }
}
