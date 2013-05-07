package com.glyph.scala.lib.util.callback

import collection.mutable
import collection.mutable.ListBuffer

/**
 * @author glyph
 */
trait Callback {
  private lazy val callbacks = mutable.HashMap[Int,ListBuffer[()=>Unit]]()
  def addCallback(typ:Int)(f:()=>Unit){
    callbacks get typ match{
      case Some(list) => list += f
      case None =>{
        val list = new ListBuffer[()=>Unit]
        callbacks(typ) = list
        list += f
      }
    }
  }
  def removeCallback(typ:Int)(f:()=>Unit){
    callbacks get typ map {_ -= f}
  }
  def callback (typ:Int){
    callbacks get typ map {_.foreach {_()}}
  }
}
