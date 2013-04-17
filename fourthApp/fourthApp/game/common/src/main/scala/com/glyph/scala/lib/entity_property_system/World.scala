package com.glyph.scala.lib.entity_property_system

import com.glyph.scala.lib.event.EventManager

/**
 * @author glyph
 */
class World {
  val eventManager = new EventManager
  val componentManager = new ComponentManager
  val poolManager = new PoolManager
}
