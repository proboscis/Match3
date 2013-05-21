package com.glyph.scala.lib.engine

import com.glyph.scala.lib.event.Dispatcher
import com.glyph.scala.game.event.{EntityRemoved, EntityAdded}
import com.glyph.scala.lib.util.collection.TypeInstanceMap

/**
 * @author glyph
 */
class GameContext extends Dispatcher{
  val members = new TypeInstanceMap
  def addEntity(e:Entity){
    e.initialize()
    dispatch(new EntityAdded(e))
  }

  def removeEntity(e:Entity){
    dispatch(new EntityRemoved(e))
  }
}
