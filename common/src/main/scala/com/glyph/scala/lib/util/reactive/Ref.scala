package com.glyph.scala.lib.util.reactive

/**
 * @author glyph
 */
class Ref [T](private var ref:Varying[T]) extends Varying[T]{
  self=>
//  var ref :Varying[T,B]= initial
  val callback = notifyObservers(_)//you must make a new function to unSubscribe!
  ref.subscribe(callback)
  def current: T = ref()
  def update(v:Varying[T]){
    ref.unSubscribe(callback)
    ref = v
    ref.subscribe(callback)
    //this is not required since inDone is invoked when subscribing
    // notifyObservers(v())
  }
}
object Ref{
  def apply[T](ref:Varying[T]):Ref[T] = new Ref[T](ref)
}