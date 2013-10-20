package com.glyph.scala.lib.util.reactive

import ref.WeakReference
//TODO the observer is not released even if the reactive is disposed!!
/**
 * @author glyph
 */
trait Reactor {
  private var observers: List[Observer[_]] = Nil
  private var debugging = false
  private var debugMsg = " "

  def debugReaction(msg: String = "") {
    debugging = true
    debugMsg = msg
  }

  def reactors = observers

  def reactVar[T](v: Varying[T])(callback: (T) => Unit): Observer[T] = new Observer(v, callback)

  def reactEvent[T](src: EventSource[T])(callback: (T) => Unit): Observer[T] = new Observer(src, callback)
  def reactSome[T](v:Varying[Option[T]])(callback: (T)=>Unit):Observer[Option[T]] = new Observer[Option[T]](v,{
    case Some(r) => callback(r)
    case None => println("failed")
  })

  protected def check[T](v: Reactive[T])(callback: PartialFunction[T, Unit]): Observer[T] = new Observer(v, (t: T) => callback(t))

  protected def reactAnd[T](v: Reactive[T])(callback: (T, Observer[T]) => Unit): Observer[T] = {
    var o: Observer[T] = null
    o = new Observer(v, (t: T) => {
      callback(t, o)
    })
    o
  }

  protected def once[T](v: EventSource[T])(callback: (T) => Unit): Observer[T] = {
    reactAnd(v) {
      (t, o) => o.unSubscribe(); callback(t)
    }
  }

  protected def once(v: EventSource[Unit])(callback: => Unit): Observer[Unit] = {
    reactAnd(v) {
      (unit, o) => o.unSubscribe(); callback
    }
  }

  class Observer[T](val reactive: Reactive[T], f: (T) => Unit) {
    if (debugging) println(debugMsg + " : subscribe=>" + reactive)
    val hook = (t: T) => {
      if (debugging) println(debugMsg + ":react:" + reactive+ "(" + t + ")")
      f(t)
    }
    if(debugging)println("new Observer@%x".format(hook.hashCode())+" of "+Reactor.this)
    reactive.subscribe(hook)
    observers = this :: observers

    /**
     * stops reaction
     */
    def unSubscribe() {
      def printReactiveObservers(){
        reactive.reactiveObservers foreach {
          case WeakReference(ref) => println("%x".format(ref.hashCode())+" "+Reactor.this)
          case what => println("what a hell!"+what)
        }
      }
      if (debugging) {
        println(debugMsg + " : unSubscribe=>" + reactive+" observer@%x".format(this.hashCode()))
        printReactiveObservers()
      }
      reactive.unSubscribe(hook)
      //reactive.validateObserver()
      observers = observers filter {
        e => e ne this
      }
      if (debugging) {
        println(debugMsg + " : unSubscribe<=" + reactive)
        printReactiveObservers()
      }
    }
  }

  protected def clearReaction() {
    //println("Reactor:clearReactions:" + this)
    observers foreach {
      _.unSubscribe()
    }
    /*
    observers foreach {
      o => println("Reactor:after cleared:" + o)
    }*/
  }

  protected def stopReact(r: Reactive[_]) {
    for (obs <- observers if obs.reactive eq r) {
      obs.unSubscribe()
    }
  }
}
