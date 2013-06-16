package com.glyph.scala.lib.util.observer

import collection.mutable.ListBuffer

/**
 * @author glyph
 */
class Observable [T]{
  val observers = new ListBuffer[(T)=>Unit]
  def apply(t:T){
    observers foreach{_(t)}
  }
}
