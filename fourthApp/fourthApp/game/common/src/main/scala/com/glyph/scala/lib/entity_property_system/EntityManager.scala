package com.glyph.scala.lib.entity_property_system

import com.glyph.scala.lib.util.Indexer

/**
 * @author glyph
 */
class EntityManager {
  val entityIndexer = new Indexer(64)
  val entityPool = new Pool[Entity]
  val entities = new collection.mutable.ListBuffer[Entity]

  def createEntity(world :World):Entity={
    val entity = entityPool.obtain()
    entity.init(entityIndexer.getNext(),world)
    entity
  }

  def deleteEntity(e:Entity){
    entityIndexer.addNext(e.index)
    entityPool.free(e)
  }

  def addEntity(e: Entity){
    entities += e
  }

  def removeEntity(e:Entity){
    entities -= e
  }
}
