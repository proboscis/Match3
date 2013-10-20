package com.glyph.scala.lib.util.observer

import ref.WeakReference
import collection.immutable.Queue

/**
 * @author glyph
 */
class Observable[T] {
  type O = Observing#Observer[T]
  var observers = Queue[WeakReference[O]]()

  def subscribe(o: O) {
    observers = observers.enqueue(WeakReference(o))
  }

  def unSubscribe(o: O) {
   // println("unsb:"+o+" from "+collectRefs)
    observers = observers filter {case WeakReference(ref)=>ref != o}
   // println(" => "+collectRefs)

   // def collectRefs = observers.collect{case WeakReference(ref)=>ref}
  }

  def apply(t: T) {
    notifyObservers(t)
  }
  def notifyObservers(t:T){
    observers = observers diff (observers filter {
      case WeakReference(ref) => false
      case _ => println("lost ref!"); true
    })
    observers foreach {
      case WeakReference(ref) => ref(t)
    }
  }
}
