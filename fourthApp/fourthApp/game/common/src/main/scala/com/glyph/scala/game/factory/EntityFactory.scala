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
  val iDTransform = pkg.getMemberIndex[DTransform]
  val iRenderer = pkg.getInterfaceIndex[Renderer]
  val iDungeonActor = pkg.getInterfaceIndex[DungeonActor]
  val iActorController = pkg.getInterfaceIndex[ActorController]
  val iDungeonMap = pkg.getMemberIndex[DungeonMap]

  def empty():Entity = {
    pkg.obtain()
  }
  def character():Entity={
    val e = pkg.obtain()
    e.setMemberI(iTransform,new Transform)
    e.setMemberI(iDTransform,new DTransform)
    e.setInterfaceI(iRenderer, new Renderer(new SimpleRenderer))
    e.setInterfaceI(iDungeonActor, new DungeonActor)
    e.setInterfaceI(iActorController,new ActorController(game))
    e
  }
  def dungeon():Entity={
    val e = pkg.obtain()
    e.setMemberI(iDungeonMap,new DungeonMap)
    e.setInterfaceI(iRenderer,new Renderer(new DungeonRenderer))
    e
  }
}

