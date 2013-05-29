package com.glyph.scala.lib.util.scene

import com.glyph.scala.lib.util.collection.LinkedList

/**
 * @author glyph
 */
trait SceneNode extends SceneComponent{
  val components = new LinkedList[SceneComponent]
  def +=(v:SceneComponent){
    components.push(v)
    v.parent = this
  }
  def -=(v:SceneComponent){
    components.remove(v)
    v.parent = null
  }
  def clear(){
    components.clear()
  }
}
