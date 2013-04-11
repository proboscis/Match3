package com.glyph.scala.lib.entity_component_system

/**
 * @author glyph
 */
class Adapter(val entity:Entity) {
  def consistsOf(e:Entity):Boolean= e eq entity
}
