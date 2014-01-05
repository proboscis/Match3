package com.glyph.scala.lib.util.reactive

import ref.WeakReference
import com.badlogic.gdx.utils.{Array => GdxArray}
import com.glyph.scala.lib.util.collection.GlyphArray
import scala.reflect.ClassTag

/**
 * you cannot make this class covariant because of the notifyObserver() method.
 * @author glyph
 */
trait Reactive[T] {
  //TODO observerやリスト、コールバックの処理を最適化する
  private val observers: GlyphArray[WeakReference[(T) => Unit]] = new GlyphArray[WeakReference[(T) => Unit]]()
  protected val removeQueue: GlyphArray[T => Unit] = new GlyphArray[(T) => Unit]()
  protected val addQueue: GlyphArray[T => Unit] = new GlyphArray[(T) => Unit]()
  protected var disposed = false
  protected var concurrent = 0
  protected var debugging = false
  protected var debugMsg = ""

  def debugReactive[R: ClassTag](str: String) {
    debugMsg = implicitly[ClassTag[R]].runtimeClass.getSimpleName + "," + str
    debugging = true
  }
  def subscribe(callback: T => Unit) {
    if (concurrent > 0) {
      addQueue add callback
      //throw new RuntimeException("concurrent modification exception!")
    } else {
      addObserver(callback)
    }
    onSubscribe(callback)
  }
  def notifyObservers(t: T) {
    import Reactive._
    stack += 1
    //println("ReactiveStack:"+stack)
    //if(debugging) println(toString)
    if (disposed) {
      println("this Reactive is already disposed! you cannot call notifyObservers after disposed\n" + this.getClass.getSimpleName)
      return
    }
    validateObserver()
    concurrent += 1
    val itr = observers.iterator()
    while(itr.hasNext){
      val value = itr.next().underlying.get()
      if(value != null){
        value(t)
      }else{
        itr.remove()
      }
    }
    concurrent -= 1
    validateObserver()
    stack -= 1
  }

  private def addObserver(f: T => Unit) {
    observers add WeakReference(f)
  }

  /**
   * does no allocation
   * @param f
   */
  private def removeObserver(f: T => Unit) {
    val itr = observers.iterator()
    while(itr.hasNext){
      val ref = itr.next
      val value = ref.underlying.get
      if(value == null || value == f)itr.remove()
    }
  }

  def reactiveObservers = observers

  def unSubscribe(callback: T => Unit) {
    if (concurrent > 0) {
      removeQueue add callback
      //throw new RuntimeException("concurrent modification exception!")
    } else {
      removeObserver(callback)
    }
  }
  protected def validateObserver() {
    if (disposed) {
      removeQueue.clear()
      addQueue.clear()
      observers.clear()
    } else {
      {// remove all observer in queue
        val itr = removeQueue.iterator()
        while(itr.hasNext){
          removeObserver(itr.next())
        }
        removeQueue.clear()
      }
      {
        val itr = addQueue.iterator()
        while(itr.hasNext){
          addObserver(itr.next())
        }
        addQueue.clear()
      }
    }
  }


  def dispose() {
    //println("dispose:"+this.getClass.getSimpleName)
    disposed = true
    validateObserver()
  }

  override def toString: String =
    if (!debugging) {
      super.toString
    } else {
      getClass.getSimpleName + "@%x \nmsg:%s".format(System.identityHashCode(this), debugMsg)
    }
  def onSubscribe(cb:T=>Unit):Unit
}

object Reactive {
  //TODO androidではなぜかこのスタックが32まで積まれてしまう
  var stack = 0
}

