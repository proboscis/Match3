package com.glyph.scala.game.component.controllers

import com.glyph.scala.lib.entity_component_system.Entity
import com.glyph.scala.game.component.{DungeonActor, Transform}

/**
 * @author glyph
 */
class AbstractController {
  var owner: Entity = null
  var transform: Transform = null
  var dungeonActor: DungeonActor = null

  def update(delta: Float) {
  }

  def initialize(owner: Entity) {
    this.owner = owner
    transform = owner.directGet[Transform]
    dungeonActor = owner.directGet[DungeonActor]
  }

  def dispose(owner: Entity) {
    this.owner = null
    transform = null
    dungeonActor = null
  }
}

