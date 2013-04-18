package com.glyph.scala.lib.entity_property_system

import java.util
import com.glyph.scala.Glyph

/**
 * @author glyph
 */
class SystemManager(world:World){
  val mSystemMap = new util.HashMap[Manifest[_],GameSystem]
  def addSystem[T<:GameSystem](system:T)(implicit typ:Manifest[T]){
    mSystemMap.put(typ,system)
    system.onAddedToWorld(world)
  }
  def removeSystem[T<:GameSystem](system:T)(implicit typ:Manifest[T]){
    mSystemMap.remove(typ)
    system.onRemovedFromWorld(world)
  }
  def update(delta: Float){

  }
}
