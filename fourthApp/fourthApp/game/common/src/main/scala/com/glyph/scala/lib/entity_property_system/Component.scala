package com.glyph.scala.lib.entity_property_system

import com.glyph.scala.lib.util.{DeprecatedPoolable, Chainable}

/**
 * @author glyph
 */
class Component extends DeprecatedPoolable with Chainable{
  @Deprecated
  var owner :Entity = null

  /**
   * called when this component is set<br>
   *   or the owner is modified
   * @param owner
   */
  def onOwnerModified(owner: Entity){
   // this.owner = owner
  }

  /**
   * called when the owner is freed
   */
  def free() {
    this.owner = null
  }
}
