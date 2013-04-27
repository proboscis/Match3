package com.glyph.scala.game.system

import com.glyph.scala.game.event.{EntityRemoved, EntityAdded}
import com.glyph.scala.lib.engine.EntityPackage
import com.glyph.scala.game.GameContext
import com.glyph.scala.Glyph

/**
 * @author glyph
 */
abstract class EntitySystem(game: GameContext, pkg: EntityPackage) {
  game.eventManager += onAddEntity
  game.eventManager += onRemoveEntity

  Glyph.log(manifest[this.type].runtimeClass.getSimpleName,"construct")
  def onAddEntity(e: EntityAdded): Boolean

  def onRemoveEntity(e: EntityRemoved): Boolean

  def dispose() {
    game.eventManager -= onAddEntity
    game.eventManager -= onRemoveEntity
  }
}
