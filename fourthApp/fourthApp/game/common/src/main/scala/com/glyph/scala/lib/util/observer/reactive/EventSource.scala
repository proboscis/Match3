package com.glyph.scala.lib.util.observer.reactive

/**
 * @author glyph
 */
class EventSource[T] extends Reactive[T] {
  self=>
  //TODO GameOver 画面の実装、カード効果の実装
  //TODO イベントドリブンの実装
  def emit(event:T){
    notifyObservers(event)
  }
  def ->[B](f:T=>B):EventSource[B]={
    new EventSource[B] with Reactor{
      react(self){
        e => emit(f(e))
      }
    }
  }
  //you can implement combinator and mapper operators
}