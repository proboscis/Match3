package com.glyph.scala.game.system

import com.glyph.scala.lib.entity_component_system.{Entity, GameSystem, EntityContainer}
import com.glyph.scala.game.adapter.ActorAdapter
import com.glyph.scala.game.component.GameActor

/**
 * adds an GameActor into game surface
 * @author glyph
 */
class ActorManageSystem extends GameSystem{
  override def onAddEntity(container: EntityContainer, entity: Entity) {
    super.onAddEntity(container, entity)
  }

  override def onRemoveEntity(container: EntityContainer, entity: Entity) {
    super.onRemoveEntity(container, entity)
  }
}
