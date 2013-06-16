package com.glyph.scala.lib.util.callback

/**
 * @author glyph
 */
class Callback1[T] {
  var function = (t:T)=>{}
  def update(t:(T)=>Unit){
    function = t
  }
  def apply(t:(T)=>Unit){
    function = t
  }
  def apply(t:T){
    function(t)
  }
}
