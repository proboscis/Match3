package com.glyph.scala.lib.util.observer

import collection.mutable.ListBuffer

/**
 * @author glyph
 */
trait Observing {
  private val observers = new ListBuffer[Observer[_]]
  def observe[T](o:Observable[T])(f: (T)=>Unit):Observer[T] ={
    val oo = new Observer(o,f)
    observers += oo
    oo
  }
  def clearObserver(){
    observers foreach {_.remove()}
  }
  class Observer[T](o:Observable[T],f:(T)=>Unit){
    o.observers += f
    def remove(){
      o.observers -= f
      observers -= this
    }
  }
}
