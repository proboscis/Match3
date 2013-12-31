package com.glyph.scala.lib.util.reactive


/**
 * @author glyph
 */
class EventSource[T] extends Reactive[T] {
  self =>
  //TODO GameOver 画面の実装、カード効果の実装
  //TODO イベントドリブンの実装
  def emit(event: T) {
    notifyObservers(event)
  }


  def onSubscribe(cb: (T) => Unit): Unit = {}

  def map[B](f: T => B): EventSource[B] = {
    new EventSource[B] with Reactor {
      reactEvent(self) {
        e => emit(f(e))
      }
    }
  }

  def ~[B](src: EventSource[B]): EventSource[Any] = {
    new EventSource[Any] with Reactor {
      reactEvent(self) {
        e => emit(e)
      }
      reactEvent(src) {
        e => emit(e)
      }
    }
  }

  def filter(f: PartialFunction[T, Boolean]): EventSource[T] = {
    new EventSource[T] with Reactor {
      reactEvent(self) {
        e => if (f(e)) emit(e)
      }
    }
  }

  //you can implement combinator and mapper operators
}

object EventSource {
  def apply[T](debug:String = ""): EventSource[T] = {
    val result = new EventSource[T]
    if (debug != "")result.debugReactive(debug)
    result
  }
}