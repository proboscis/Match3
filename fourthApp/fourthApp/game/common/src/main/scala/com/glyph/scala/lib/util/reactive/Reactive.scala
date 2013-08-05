package com.glyph.scala.lib.util.reactive

import ref.WeakReference


/**
 * @author glyph
 */
trait Reactive[T] {
  private var observers: List[WeakReference[T => Unit]] = Nil
  private var removeQueue: List[T => Unit] = Nil
  private var addQueue: List[T => Unit] = Nil
  private var disposed = false
  private var concurrent = 0
  private var debugging = false
  private var debugMsg = ""
  def debugReactive(str:String){
    debugMsg = str
    debugging = true
  }


  def subscribe(callback: T => Unit) {
    if (concurrent > 0) {
      addQueue = callback :: addQueue
      //throw new RuntimeException("concurrent modification exception!")
    }else{
      addObserver(callback)
    }
  }
  private def addObserver(f:T=>Unit){
    observers = WeakReference(f) :: observers
  }
  private def removeObserver(f:T=>Unit){
    observers = observers filter {
      case WeakReference(ref) => ref != f
      case _ => false
    }
  }

  def reactiveObservers = observers

  def unSubscribe(callback: T => Unit) {
    if (concurrent > 0) {
      removeQueue = callback :: removeQueue
      //throw new RuntimeException("concurrent modification exception!")
    }else{
      removeObserver(callback)
    }
  }

  val log = (str: String) => println("reactive:" + str)

  private def validateObserver() {
    if (disposed) {
      removeQueue = Nil
      addQueue = Nil
      observers = Nil
    } else {
      removeQueue foreach {
        func =>
          removeObserver(func)
      }
      removeQueue = Nil
      addQueue foreach {
        f => addObserver(f)
      }
      addQueue = Nil
    }
  }

  def notifyObservers(t: T) {
    if (disposed) throw new RuntimeException("this Reactive is already disposed! you cannot call notifyObservers after disposed\n" + this.getClass.getSimpleName)
    validateObserver()
    concurrent += 1
    observers = observers filter {
      wRef => wRef.get match {
        case Some(ref) => ref(t); true
        case _ => false
      }
    }
    concurrent -= 1
    validateObserver()
  }

  def dispose() {
    //println("dispose:"+this.getClass.getSimpleName)
    disposed = true
    validateObserver()
  }

  override def toString: String =
    if(!debugging){
      super.toString
    }else{
      debugMsg+" : "+super.toString
    }
}