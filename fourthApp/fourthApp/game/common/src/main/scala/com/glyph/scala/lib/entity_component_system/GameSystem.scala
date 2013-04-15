package com.glyph.scala.lib.entity_component_system

/**
 * @author glyph
 */
trait GameSystem {
  def dispose(){}
  def update(delta:Float){}
}
