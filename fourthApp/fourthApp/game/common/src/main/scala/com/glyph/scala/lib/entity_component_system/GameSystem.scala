package com.glyph.scala.lib.entity_component_system

/**
 * @author glyph
 */
class GameSystem(val game:GameContext) {
  def dispose(){}
  def update(delta:Float){}
}

