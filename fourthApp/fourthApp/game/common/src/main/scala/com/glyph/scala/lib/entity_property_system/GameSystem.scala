package com.glyph.scala.lib.entity_property_system

import java.util
import com.glyph.libgdx.util.ArrayStack
import com.glyph.scala.lib.util.Foreach

/**
 * @author glyph
 */
class GameSystem(types: Manifest[_ <: Component]*) {
  var interests: util.BitSet = null
  var world: World = null
  private var mEnabled = true
  protected val entities = new ArrayStack[Entity] with Foreach[Entity]

  def onAddedToWorld(w: World) {
    world = w
    interests = world.entityFactory.componentManager.getFilter(types)
  }

  def onAddEntity(entity:Entity){
    if(entity.hasAllComponents(interests)){
      //Glyph.log("addentity",this.getClass.getSimpleName)
      entities.push(entity)
    }
  }
  def onRemoveEntity(entity:Entity){
    if (entity.hasAllComponents(interests)){
      entities.remove(entity)
    }
  }

  def update(delta:Float){}

  def enable() {
    mEnabled = true
  }

  def disable() {
    mEnabled = false
  }

  def isEnabled = mEnabled
}
