package com.glyph.scala.game.factory

import com.glyph.scala.lib.engine.{Entity, EntityPackage}
import com.glyph.scala.game.component._
import com.glyph.scala.game.GameContext
import controller.ActorController
import dungeon_actor.{BaseActor, DungeonActor}
import renderer.{Renderer, SimpleRenderer}
import update.Update
import value.{Transform, DTransform}

/**
 * @author glyph
 */
class EntityFactory(game:GameContext,pkg:EntityPackage) {
  val iTransform = pkg.getIndex[Transform]
  val iDTransform = pkg.getIndex[DTransform]
  val iRenderer = pkg.getIndex[Renderer]
  val iDungeonActor = pkg.getIndex[DungeonActor]
  val iActorController = pkg.getIndex[ActorController]
  val iUpdate = pkg.getIndex[Update]

  def empty():Entity = {
    pkg.obtain()
  }
  def character():Entity={
    val e = pkg.obtain()
    e.setI(iTransform,new Transform)
    e.setI(iDTransform,new DTransform)
    e.setI(iRenderer, new Renderer(e) with SimpleRenderer)
    e.setI(iDungeonActor, new DungeonActor(e) with BaseActor)
    e
  }
  def player():Entity= {
    val e = character()
    e
  }
}

