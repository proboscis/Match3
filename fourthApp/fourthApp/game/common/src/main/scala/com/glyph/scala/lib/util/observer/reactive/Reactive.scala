package com.glyph.scala.lib.util.observer.reactive

import ref.WeakReference


/**
 * @author glyph
 */
trait Reactive[T] {
  private var observers: List[WeakReference[T => Unit]] = Nil
  private var removeQueue: List[T => Unit] = Nil
  private var addQueue: List[T => Unit] = Nil

  def subscribe(callback: T => Unit) {
    addQueue = callback :: addQueue
  }

  def reactiveObservers = observers

  def unSubscribe(callback: T => Unit) {
    removeQueue = callback :: removeQueue
  }

  private def validateObserver() {
    removeQueue foreach {
      func =>
        observers = observers filter {
          case WeakReference(ref) => ref != func
          case _=> false
        }
    }
    removeQueue = Nil
    addQueue foreach {
      f => observers = WeakReference(f) :: observers
    }
    addQueue = Nil
  }

  def notifyObservers(t: T) {
    validateObserver()
    observers = observers filter {
      wRef => wRef.get match {
        case Some(ref) => ref(t); true
        case _ => false
      }
    }
    validateObserver()
  }
}