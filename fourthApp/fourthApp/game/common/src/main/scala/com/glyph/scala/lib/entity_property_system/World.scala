package com.glyph.scala.lib.entity_property_system

import com.glyph.scala.lib.event.EventManager
import collection.mutable.ListBuffer

/**
 * @author glyph
 */
class World {
  val INITIAL_NUMBER_OF_ENTITY = 1024
  val entityFactory = new EntityFactory(this)
  val eventManager = new EventManager
  val poolManager = new PoolManager

  private val entityList = new ListBuffer[Entity]
  def addEntity(e:Entity)=entityList += e
  def removeEntity(e:Entity) = entityList -= e
  def entities:Seq[Entity] = entityList
}
