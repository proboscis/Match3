package com.glyph.scala.game

import com.glyph.scala.lib.entity_component_system.Entity
import component.{GameActorRenderer, GameActor, DungeonActor}

/**
 * @author glyph
 */
object EntityFactory{
  def dungeon:Entity={
    val e = new Entity
    e.initialize()
    e
  }
  def createNewCharacter:Entity={
    val e = new Entity
    e.register(new DungeonActor)
    e.register(new GameActor)
    e.register(new GameActorRenderer)
    e.initialize()
    e
  }
}
