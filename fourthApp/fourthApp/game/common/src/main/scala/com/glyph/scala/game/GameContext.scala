package com.glyph.scala.game

import com.glyph.scala.lib.event.EventManager
import com.glyph.scala.lib.engine.Entity
import event.{EntityRemoved, EntityAdded}

/**
 * @author glyph
 */
class GameContext {
  val eventManager = new EventManager()

  def addEntity(e:Entity){
    eventManager.dispatch(new EntityAdded(e))
  }

  def removeEntity(e:Entity){
    eventManager.dispatch(new EntityRemoved(e))
  }
}
