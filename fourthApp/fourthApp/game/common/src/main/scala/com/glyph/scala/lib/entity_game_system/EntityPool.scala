package com.glyph.scala.lib.entity_game_system

import com.glyph.scala.lib.util.pool.AbstractPool

/**
 * @author glyph
 */
class EntityPool(pkg: EntityPackage) extends AbstractPool[Entity] {
  def createNewInstance(): Entity = new Entity(pkg, this)
}
