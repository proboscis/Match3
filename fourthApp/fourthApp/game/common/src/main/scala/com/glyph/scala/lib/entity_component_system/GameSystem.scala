package com.glyph.scala.lib.entity_component_system

/**
 * @author glyph
 */
trait GameSystem {
  def onAddEntity(container:EntityContainer,entity:Entity){}
  def onRemoveEntity(container:EntityContainer,entity:Entity){}
  def update(container:EntityContainer){}
}
