package com.glyph.scala.game

import com.glyph.scala.lib.entity_component_system.{EntityManager, Entity}
import component._
import component.renderer.Renderer
import controllers.{Controller, PlayerController}

/**
 * @author glyph
 */
object EntityFactory {
  def createDungeon: Entity = {
    val e = new Entity
    e.register(new DungeonGame)
    e
  }

  def createNewCharacter: Entity = {
    val e = new Entity
    e.register(new DungeonActor)
    e.register(new Transform)
    e.register(new Renderer)
    e
  }

  def createPlayer: Entity = {
    val e = createNewCharacter
    e.register(new Tag("player"))
    e.register(new Controller(new PlayerController))
    e
  }
}
