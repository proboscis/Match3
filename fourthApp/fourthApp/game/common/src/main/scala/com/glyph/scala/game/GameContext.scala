package com.glyph.scala.game

import card.CardDeque
import com.glyph.scala.lib.event.EventManager
import com.glyph.scala.lib.engine.Entity
import event.{EntityRemoved, EntityAdded}
import com.glyph.scala.lib.util.TypeInstanceMap

/**
 * @author glyph
 */
class GameContext{
  val eventManager = new EventManager()
  val systems = new TypeInstanceMap
  val playerDeque = new CardDeque
  def addEntity(e:Entity){
    eventManager.dispatch(new EntityAdded(e))
  }

  def removeEntity(e:Entity){
    eventManager.dispatch(new EntityRemoved(e))
  }
}
