package com.glyph.scala.lib.util.scene

import scala.collection.mutable.ListBuffer


/**
 * @author glyph
 */
trait SceneNode extends SceneComponent{
  val components = new ListBuffer[SceneComponent]
  def +=(v:SceneComponent){
    components+=(v)
    v.parent = this
  }
  def -=(v:SceneComponent){
    components-=(v)
    v.parent = null
  }
  def clear(){
    components.clear()
  }
}
