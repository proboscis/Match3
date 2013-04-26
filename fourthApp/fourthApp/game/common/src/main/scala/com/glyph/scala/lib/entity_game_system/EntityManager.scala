package com.glyph.scala.lib.entity_game_system

import com.glyph.libgdx.util.ArrayStack
/**
 * @author glyph
 */
class EntityManager (){
  val entities = new ArrayStack[Entity]
  def addEntity(e:Entity){
    entities.push(e)
  }
  def removeEntity(e:Entity){
    entities.remove(e)
  }
}
