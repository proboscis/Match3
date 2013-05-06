package com.glyph.scala.lib.event

import collection.mutable
import com.glyph.scala.lib.util.LinkedList

/**
 * @author glyph
 */
trait Dispatcher{
  private val listenerMap = mutable.HashMap[Manifest[_],com.glyph.scala.lib.util.LinkedList[Any]]()
  private lazy val children = new LinkedList[Dispatcher]
  def +=[T:Manifest](f:(T)=>Unit){
    listenerMap get implicitly[Manifest[T]] match{
      case Some(list) => list push f
      case None =>{
        val list =  new LinkedList[Any]
        listenerMap(implicitly[Manifest[T]]) =list
        list push f
      }
    }
  }

  def -=[T:Manifest](f:(T)=>Unit){
    listenerMap get implicitly[Manifest[T]] map {_.remove(f)}
  }
  def dispatch[T:Manifest](v:T){
    listenerMap get implicitly[Manifest[T]] map {_.foreach{_.asInstanceOf[(T)=>Unit](v)}}
    if(!children.isEmpty){
      children.foreach{_.dispatch(v)}
    }
  }
  def addDispatcher(d:Dispatcher){
    children push d
  }
  def removeDispatcher(d:Dispatcher){
    children remove d
  }
}
