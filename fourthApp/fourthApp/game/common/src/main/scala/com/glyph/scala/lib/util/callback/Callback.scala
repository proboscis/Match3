package com.glyph.scala.lib.util.callback

import collection.mutable
import collection.mutable.ListBuffer

/**
 * @author glyph
 */
trait Callback {
  private val callbacks = mutable.HashMap[String,ListBuffer[()=>Unit]]()
  def addCallback(typ:String)(f:()=>Unit){
    callbacks get typ match{
      case Some(list) => list += f
      case None =>{
        val list = new ListBuffer[()=>Unit]
        callbacks(typ) = list
        list += f
      }
    }
  }
  def removeCallback(typ:String)(f:()=>Unit){
    callbacks get typ map {_ -= f}
  }
}
