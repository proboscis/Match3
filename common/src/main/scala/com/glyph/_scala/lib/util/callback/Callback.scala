package com.glyph._scala.lib.util.callback

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
