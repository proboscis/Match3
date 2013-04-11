package com.glyph.scala.game.system

import com.glyph.scala.lib.entity_component_system.{Entity, GameSystem, EntityContainer}
import com.glyph.libgdx.surface.Surface
import com.glyph.scala.game.component.GameActor
import com.glyph.scala.Glyph

/**
 * adds an GameActor into game surface
 * @author glyph
 */
class ActorManageSystem(surface: Surface) extends GameSystem{
  private val TAG = "ActorManageSystem"
  override def onAddEntity(container: EntityContainer, entity: Entity) {
    super.onAddEntity(container, entity)
    val actor = entity.mayBeGet[GameActor]
    for (a <- actor){
      surface.addActor(a)
      Glyph.log(TAG,"added an actor to the surface")
    }
  }

  override def onRemoveEntity(container: EntityContainer, entity: Entity) {
    super.onRemoveEntity(container, entity)
    val actor = entity.mayBeGet[GameActor]
    for (a <- actor){
      surface.removeActor(a)
      Glyph.log(TAG,"removed an actor from the surface")
    }
  }
}
