package com.glyph.scala.lib.util.observer


/**
 * @author glyph
 */
trait Observing {
  private var observers: List[Observer[_]] = Nil

  def observe[T](o: Observable[T])(f: (T) => Unit): Observer[T] = {
    val oo = new Observer(o, f)
    observers = oo :: observers
    oo
  }

  def clearObservers() {
    observers foreach {
      _.remove()
    }
    observers = Nil
  }

  def removeObserver(o: Observable[_]) {
    observers = observers filter{
      observer =>{
        if(observer.o == o){
          observer.remove()
          false
        }else true
      }
    }
  }

  class Observer[T](val o: Observable[T], f: (T) => Unit) {
    o.subscribe(this)

    def remove() {
      o.unSubscribe(this)
      observers = observers diff this :: Nil
    }

    def apply(param: T) {
      f(param)
    }
  }

}
