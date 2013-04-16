package com.glyph.scala.game

import com.glyph.scala.lib.entity_component_system.{EntityManager, Entity}
import component._
import renderer.{SimpleRenderer, DungeonRenderer, Renderer}
import controllers.{Controller, PlayerController}
import com.glyph.scala.Glyph

/**
 * @author glyph
 */
object EntityFactory {
  private val TAG = "EntityFactory"

  def createEmpty:Entity={
    val e = new Entity
    e.register(new Transform)
    e
  }

  def createDungeon: Entity = {
    Glyph.log(TAG,"create dungeon")
    val e = new Entity
    e.register(new DungeonGame)
    e.register(new Transform)
    e.register(new Renderer(new DungeonRenderer))
    e
  }

  def createNewCharacter: Entity = {
    val e = new Entity
    e.register(new DungeonActor)
    e.register(new Transform)
    e.register(new Renderer(new SimpleRenderer))
    e
  }

  def createPlayer: Entity = {
    val e = createNewCharacter
    e.register(new Tag("player"))
    e.register(new Controller(new PlayerController))
    e
  }
}

