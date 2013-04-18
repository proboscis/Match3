package com.glyph.scala.lib.entity_property_system

import java.util

/**
 * @author glyph
 */
class GameSystem(types:Manifest[_<:Component]*){
  var interests :util.BitSet=null
  var world :World= null
  def onAddedToWorld(w:World){
    world = w
    world.entityFactory.componentManager.getFilter(types)
  }
  def onRemovedFromWorld(w:World){}
  def beginProcess:Unit={}
  def process:Unit={}
  def endProcess:Unit={}
}
