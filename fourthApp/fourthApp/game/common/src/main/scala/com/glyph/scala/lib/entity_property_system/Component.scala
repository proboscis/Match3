package com.glyph.scala.lib.entity_property_system

import com.glyph.scala.lib.util.{Poolable, Chainable}

/**
 * @author glyph
 */
class Component extends Poolable with Chainable{
  var owner :Entity = null
  def onOwnerModified(owner: Entity){
    this.owner = owner
  }

  def free() {
    this.owner = null
  }

}
