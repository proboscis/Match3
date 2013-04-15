package com.glyph.scala.game

import com.glyph.scala.lib.entity_component_system.EntityManager
import com.glyph.scala.event.EventManager

/**
 * Created with IntelliJ IDEA.
 * User: glyph
 * Date: 13/04/02
 * Time: 15:19
 * To change this template use File | Settings | File Templates.
 */
class GameContext {
  val eventManager = new EventManager
  val entityContainer = new EntityManager(this)
}
