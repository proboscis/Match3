package com.glyph.scala.game.factory

import com.glyph.scala.lib.engine.{Entity, EntityPackage}
import com.glyph.scala.game.component.{DungeonMap, DTransform, Transform}
import com.glyph.scala.game.interface.renderer.{DungeonRenderer, SimpleRenderer, Renderer}
import com.glyph.scala.game.interface.{ActorController, DungeonActor}
import com.glyph.scala.game.GameContext

/**
 * @author glyph
 */
class EntityFactory(game:GameContext,pkg:EntityPackage) {
  val iTransform = pkg.getMemberIndex[Transform]
  def empty():Entity = {
    pkg.obtain()
  }
  def character():Entity={
    val e = pkg.obtain()
    e.setMember(new Transform)
    e.setMember(new DTransform)
    e.setInterface(new Renderer(new SimpleRenderer))
    e.setInterface(new DungeonActor)
    e.setInterface(new ActorController(game))
    e
  }
  def dungeon():Entity={
    val e = pkg.obtain()
    e.setMember(new DungeonMap)
    e.setInterface(new Renderer(new DungeonRenderer))
    e
  }
}

