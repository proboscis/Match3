package com.glyph.scala.lib.entity_game_system

import com.glyph.scala.lib.util.laz.LazyValue

/**
 * @author glyph
 */
abstract trait EntityInterface extends LazyValue[Entity]{
  private var mOwner: Entity = null
  /**
   * you cannot override!
   * @param entity
   */
  final def onAttached(entity: Entity) {
    mOwner = entity
  }
  def owner = mOwner
}
