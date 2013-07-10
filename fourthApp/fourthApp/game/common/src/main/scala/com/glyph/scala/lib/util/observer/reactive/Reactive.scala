package com.glyph.scala.lib.util.observer.reactive

import ref.WeakReference


/**
 * @author glyph
 */
trait Reactive[T] {
  //TODO Reactive Data-flowの実装
  private var observers: List[WeakReference[T => Unit]] = Nil
  private var unSubscribeQueue: List[T => Unit] = Nil

  def subscribe(callback: T => Unit) {
    //println("sub=>"+System.identityHashCode(callback))
    observers = WeakReference(callback) :: observers
  }

  def reactiveObservers = observers

  def unSubscribe(callback: T => Unit) {
    unSubscribeQueue = callback :: unSubscribeQueue
  }

  private def validateObserver() {
    unSubscribeQueue = unSubscribeQueue.dropWhile {
      func =>
        observers = observers filter {
          case WeakReference(ref) => ref != func
        }
        true
    }
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