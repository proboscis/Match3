package com.glyph.scala.lib.entity_property_system

import com.glyph.scala.lib.event.EventManager
import java.util
import com.glyph.libgdx.util.ArrayStack
import com.glyph.scala.lib.util.Foreach

/**
 * @author glyph
 */
class World {
  val INITIAL_NUMBER_OF_ENTITY = 1024
  val entityFactory = new EntityFactory(this)
  val eventManager = new EventManager
  val systemManager = new SystemManager(this)
  val componentManager = entityFactory.componentManager

  private val entityStack = new ArrayStack[Entity](INITIAL_NUMBER_OF_ENTITY) with Foreach[Entity]

  def entities = entityStack

  def addEntity(e: Entity) = {
    entityStack.push(e)
    systemManager.onAddEntity(e)
  }

  def removeEntity(e: Entity) = {
    entityStack.remove(e)
    systemManager.onRemoveEntity(e)
  }

  def fillWithFilteredEntity(filter: util.BitSet, fillBag: ArrayStack[Entity]) {
    fillBag.clearStack()
    var ei = 0
    while (ei < entityStack.size()) {
      val entity = entityStack.get(ei)
      if (entity.hasAllComponents(filter)){
        fillBag.push(entity)
      }
      ei += 1
    }
  }

  def registerSystem[T <: GameSystem : Manifest](system: T) = systemManager.registerSystem(system)

  def updateSystems(delta: Float) = systemManager.update(delta)

  def componentMapper[T<:Component: Manifest] = componentManager.getComponentMapper[T]

  def componentPool[T <: Component : Manifest] = componentManager.componentPool[T]

  def componentIndex[T <: Component : Manifest] = componentManager.componentIndex[T]
}
