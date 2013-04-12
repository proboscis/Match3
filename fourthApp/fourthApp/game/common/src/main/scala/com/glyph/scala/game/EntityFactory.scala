package com.glyph.scala.game

import com.glyph.scala.lib.entity_component_system.{EntityContainer, Entity}
import component.{DungeonGame, Renderer, Transform, DungeonActor}

/**
 * @author glyph
 */
object EntityFactory {
  def dungeon(g:GameContext): Entity = {
    val e = new Entity
    e.register(new DungeonGame)
    e.initialize(g)
    e
  }

  def createNewCharacter(g:GameContext): Entity = {
    val e = new Entity
    e.register(new DungeonActor)
    e.register(new Transform)
    e.register(new Renderer)
    e.initialize(g)
    e
  }
}
