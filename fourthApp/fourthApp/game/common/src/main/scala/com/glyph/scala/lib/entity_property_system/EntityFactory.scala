package com.glyph.scala.lib.entity_property_system

import com.glyph.scala.lib.util.{Pool, Indexer}

/**
 * @author glyph
 */
class EntityFactory(world:World) {
  val entityIndexer = new Indexer(world.INITIAL_NUMBER_OF_ENTITY)
  val entityPool = new Pool[Entity]
  val componentManager = new ComponentManager(world.INITIAL_NUMBER_OF_ENTITY)

  def createEntity():Entity={
    val entity = entityPool.obtain()
    entity.init(entityIndexer.getNext(),world)
    entity
  }

  def deleteEntity(e:Entity){
    entityIndexer.addNext(e.index)
    componentManager.freeAllComponent(e);
    entityPool.free(e)
  }
}
