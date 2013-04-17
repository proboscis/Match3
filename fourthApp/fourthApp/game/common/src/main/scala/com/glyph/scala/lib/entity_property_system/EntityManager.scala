package com.glyph.scala.lib.entity_property_system

import com.glyph.scala.lib.util.Indexer

/**
 * @author glyph
 */
class EntityManager(world:World) {
  val entityIndexer = new Indexer(1024)
  val entityPool = new Pool[Entity]
  val entities = new collection.mutable.ListBuffer[Entity]
  val componentManager = new ComponentManager

  def createEntity():Entity={
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
