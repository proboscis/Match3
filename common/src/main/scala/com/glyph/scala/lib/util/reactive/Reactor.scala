package com.glyph.scala.lib.util.reactive

import ref.WeakReference
import scala.util.{Success, Failure, Try}
import java.util.Observer
import com.glyph.scala.lib.util.collection.GlyphArray
import com.glyph.scala.lib.util.pool.{Poolable, Pool, Pooling}
import com.glyph.scala.lib.util.{Logging, reactive}
import com.glyph.scala.lib.util

//TODO the observer is not released even if the reactive is disposed!!
/**
 * @author glyph
 */
trait Reactor {
  private val observers: com.badlogic.gdx.utils.Array[Observer[_]] = new com.badlogic.gdx.utils.Array[Observer[_]]()
  var reactorDebugging = false
  var reactorDebugMsg = " "

  import Reactor._
  def debugReaction(msg: String = "") {
    reactorDebugging = true
    reactorDebugMsg = msg
  }

  def reactors = observers

  /**
   * callback is invoked when the varying has changed.
   * its varying's responsibility to invoke this callback.
   * @param v
   * @param callback
   * @tparam T
   * @return
   */
  def reactVar[T](v: Varying[T])(callback: (T) => Unit): Observer[T] = {
    val observer = obtainObserver[T]
    observer.init(this,v,callback)
    observer
  }

  def reactEvent[T](src: EventSource[T])(callback: (T) => Unit): Observer[T] ={
    val observer = obtainObserver[T]
    observer.init(this,src,callback)
    observer
  }

  /**
   * this creates an annonfun
   * @param v
   * @param callback
   * @tparam T
   * @return
   */
  def reactSome[T](v: Varying[Option[T]])(callback: (T) => Unit): Observer[Option[T]] ={
    val observer = obtainObserver[Option[T]]
    observer.init(this,v,opt =>{
      if(opt.isDefined)callback(opt.get)
    })
    observer
  }

  /**
   * this creates an annonfun
   * @param v
   * @param cb
   * @tparam T
   * @tparam R
   * @return
   */
  def reactSuccess[T,R](v: Varying[Try[T]])(cb: (T) => R): Observer[Try[T]] = {
    val observer = obtainObserver[Try[T]]
    observer.init(this,v,t =>{
      if(t.isSuccess)cb(t.get)else{
        t.failed.get.printStackTrace()
      }
    })
    observer
  }

  /**
   * creates an annonfun
   * @param v
   * @param callback
   * @tparam T
   * @return
   */
  protected def reactAnd[T](v: Reactive[T])(callback: (T, Observer[T]) => Unit): Observer[T] = {
    val observer = obtainObserver[T]
    observer.init(this,v,(t:T)=>{
      callback(t, observer)
    })
    observer
  }

  /**
   * creates two annonfun
   * @param v
   * @param callback
   * @tparam T
   * @return
   */
  protected def once[T](v: EventSource[T])(callback: (T) => Unit): Observer[T] = {
    reactAnd(v) {
      (t, o) => o.unSubscribe(); callback(t)
    }
  }

  /**
   * creates two annonfun
   * @param v
   * @param callback
   * @return
   */
  protected def once(v: EventSource[Unit])(callback: => Unit): Observer[Unit] = {
    reactAnd(v) {
      (unit, o) => o.unSubscribe(); callback
    }
  }

  

  def clearReaction() {
    val itr = observers.iterator()
    while(itr.hasNext)itr.next().unSubscribe()
  }

  def stopReact(r: Reactive[_]) {
    val itr = observers.iterator()
    while(itr.hasNext){
      val next = itr.next()
      if(next.reactive eq r)next.unSubscribe()
    }
  }
}
class Observer[T] extends Poolable with Logging{
  var partner:Reactor = null
  var reactive: Reactive[T] = null
  var f:T=>Unit = null// you cannot hook this function or the specialization will corrupt
  def init(part:Reactor,reactive:Reactive[T],func:T=>Unit){
    this.partner = part
    this.reactive = reactive
    f = func
    reactive.subscribe(f)
    partner.reactors add this
  }

  def reset(){
    partner = null
    reactive = null
  }

  /**
   * stops reaction
   */
  def unSubscribe() {
    def printReactiveObservers() {
      val itr = reactive.reactiveObservers.iterator()
      while(itr.hasNext){
        val value = itr.next().underlying.get()
        if(value != null){
          println("%x".format(value.hashCode()) + " " + partner)
        }else{
          println("what a hell!" + null)
        }
      }
    }
    if (partner.reactorDebugging) {
      println(partner.reactorDebugMsg + " : unSubscribe=>" + reactive + " observer@%x".format(this.hashCode()))
      printReactiveObservers()
    }
    reactive.unSubscribe(f)
    //reactive.validateObserver()
    partner.reactors.removeValue(this,false)
    if (partner.reactorDebugging) {
      println(partner.reactorDebugMsg + " : unSubscribe<=" + reactive)
      printReactiveObservers()
    }
    //freeToPool()
  }
}
object Reactor{
  //TODO you can't pool the observers since they are
  implicit object PoolingObserver extends Pooling[Observer[_]]{
    def newInstance: Observer[_] = new Observer[Any]
    def reset(tgt: Observer[_]): Unit = tgt.reset()
  }
  val observerPool = Pool[Observer[_]](10000)
  def obtainObserver[T]:Observer[T] = new Observer[T]//observerPool.auto.asInstanceOf[Observer[T]]
}