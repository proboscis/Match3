package com.glyph.scala.lib.util.callback

import com.glyph.scala.lib.util.collection.LinkedList

/**
 * @author glyph
 */
class Callbacks{
  private val callbacks = new LinkedList[()=>Unit]
  def apply() {
    callbacks.foreach(_())
  }

  def += (f: ()=>Unit){
    callbacks.push(f)
  }
  def -= (f: ()=>Unit){
    callbacks.remove(f)
  }
  def clear(){
    callbacks.clear()
  }
}
