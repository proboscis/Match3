package com.glyph.scala.lib.util.callback

import collection.mutable

/**
 * sugar class to add callbacks
 * @author glyph
 */
class DeprecatedCallback{
  type F = ()=>Unit
  lazy val callbacks = mutable.ListBuffer[F]()
  def +=(func:F){
    callbacks += func
  }
  def -=(func:F){
    callbacks -= func
  }
  def apply(){
    callbacks.foreach {_()}
  }
}

class Callback1[T]{
  type F = (T)=>Unit
  lazy val callbacks = mutable.ListBuffer[F]()
  def +=(func:F){
    callbacks += func
  }
  def -=(func:F){
    callbacks -= func
  }
  def apply(v:T){
    callbacks.foreach {_(v)}
  }
}
