package com.glyph.scala.lib.util.callback

/**
 * @author glyph
 */
class GenericCallback[T]{
  var callback:(T)=>Unit ={(t:T)=>{}}
  def apply(func:T=>Unit){
    callback = {(t:T)=>func(t)}
  }
  def apply(t:T){
    callback(t)
  }
}
