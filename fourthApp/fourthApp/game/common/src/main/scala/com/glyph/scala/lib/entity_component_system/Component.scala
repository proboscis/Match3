package com.glyph.scala.lib.entity_component_system


/**
 * Componet of Entity
 */
class Component {
  var owner: Entity = null

  def initialize(owner: Entity) {
    this.owner = owner
  }

  def finish(owner: Entity) {
    this.owner = null
  }
}
