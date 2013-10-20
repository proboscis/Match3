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
