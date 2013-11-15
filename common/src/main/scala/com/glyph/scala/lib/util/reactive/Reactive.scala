package com.glyph.scala.lib.util.reactive

import ref.WeakReference


/**
 * you cannot make this class covariant because of the notifyObserver() method.
 * @author glyph
 */
trait Reactive[T] {
  //TODO observerやリスト、コールバックの処理を最適化する
  import Reactive._
  private var observers: List[WeakReference[T => Unit]] = Nil
  private var removeQueue: List[T => Unit] = Nil
  private var addQueue: List[T => Unit] = Nil
  private var disposed = false
  private var concurrent = 0
  private var debugging = false
  private var debugMsg = ""
  def debugReactive[R:Manifest](str:String){
    debugMsg = implicitly[Manifest[R]].runtimeClass.getSimpleName+","+str
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
    observers::= WeakReference(f)
  }
  private def removeObserver(f:T=>Unit){
    observers = observers collect{
      case weak@WeakReference(ref) if ref != f => weak
    }
  }

  def reactiveObservers = observers

  def unSubscribe(callback: T => Unit) {
    if (concurrent > 0) {
      removeQueue::=callback
      //throw new RuntimeException("concurrent modification exception!")
    }else{
      removeObserver(callback)
    }
  }

  private val log = (str: String) => println("reactive:" + str)

  private def validateObserver() {
    if (disposed) {
      removeQueue = Nil
      addQueue = Nil
      observers = Nil
    } else {
      removeQueue foreach removeObserver
      removeQueue = Nil
      addQueue foreach addObserver
      addQueue = Nil
    }
  }
  def notifyObservers(t: T) {

    stack += 1
    //println("ReactiveStack:"+stack)
    //if(debugging) println(toString)
    if (disposed){
      println("this Reactive is already disposed! you cannot call notifyObservers after disposed\n" + this.getClass.getSimpleName)
      return
    }
    validateObserver()
    concurrent += 1
    observers = observers collect {
      case weak@WeakReference(ref) =>ref(t); weak
    }
    concurrent -= 1
    validateObserver()
    stack -=1
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
      getClass.getSimpleName+"@%x \nmsg:%s".format(System.identityHashCode(this),debugMsg)
    }
}

object Reactive{
  //TODO androidではなぜかこのスタックが32まで積まれてしまう
  var stack = 0
}