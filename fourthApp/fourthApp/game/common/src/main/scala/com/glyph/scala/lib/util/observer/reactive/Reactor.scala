package com.glyph.scala.lib.util.observer.reactive

/**
 * @author glyph
 */
trait Reactor {
  private var observers: List[Observer[_]] = Nil

  def react[T](v: Reactive[T])(callback: (T) => Unit): Observer[T] = {
    val o = new Observer(v, callback)
    observers = (o :: observers)
    o
  }
  def reactIf(v:Reactive[Boolean])(callback: =>Unit):Observer[Boolean]={
    val o = new Observer(v,(b:Boolean)=>if(b)callback)
    observers = o::observers
    o
  }

  class Observer[T](varying: Reactive[T], f: (T) => Unit) {
    varying.subscribe(f)

    def unSubscribe() {
      varying.unSubscribe(f)
    }
  }

  implicit def blockToBlock[T](block: => T): Block[T] = {
    Block(block)
  }
  def clearReaction(){
    observers foreach {_.unSubscribe()}
    observers = Nil
  }
  //TODO impl remove observers...not yet



  /**
   * extractor for tuple
   */
  object ~ {
    def unapply[A, B](t: (A, B)) = Some(t)
  }
}
