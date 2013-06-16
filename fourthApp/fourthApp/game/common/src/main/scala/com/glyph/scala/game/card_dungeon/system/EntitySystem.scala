package com.glyph.scala.game.system

import com.glyph.scala.game.event.{EntityRemoved, EntityAdded}
import com.glyph.scala.lib.engine.{GameContext, EntityPackage}
import com.glyph.scala.Glyph

/**
 * @author glyph
 */
abstract class EntitySystem(game: GameContext) {
  game += onAddEntity
  game += onRemoveEntity

  Glyph.deprecatedLog(manifest[this.type].runtimeClass.getSimpleName,"construct")
  def onAddEntity(e: EntityAdded)

  def onRemoveEntity(e: EntityRemoved)

  def dispose() {
    game  -= onAddEntity
    game  -= onRemoveEntity
  }
}
