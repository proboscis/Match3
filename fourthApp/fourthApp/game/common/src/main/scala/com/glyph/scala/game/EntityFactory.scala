package com.glyph.scala.game

import com.glyph.scala.lib.entity_component_system.Entity
import component.{DungeonGame, Renderer, Transform, DungeonActor}

/**
 * @author glyph
 */
object EntityFactory {
  def dungeon: Entity = {
    val e = new Entity
    e.initialize()
    e.register(new DungeonGame)
    e
  }

  def createNewCharacter: Entity = {
    val e = new Entity
    e.register(new DungeonActor)
    e.register(new Transform)
    e.register(new Renderer)
    e.initialize()
    e
  }
}
