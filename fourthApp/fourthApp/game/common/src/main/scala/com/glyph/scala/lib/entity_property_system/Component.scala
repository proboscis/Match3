package com.glyph.scala.lib.entity_property_system

/**
 * @author glyph
 */
class Component extends Poolable{
  var owner :Entity = null
  def onOwnerStatusChanged(owner: Entity){
    this.owner = owner
  }

  def free() {
    this.owner = null
  }
}
