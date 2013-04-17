package com.glyph.scala.lib.entity_property_system

import com.glyph.scala.lib.event.EventManager

/**
 * @author glyph
 */
class World {
  val entityManager = new EntityManager(this)
  val eventManager = new EventManager
  val poolManager = new PoolManager
}
