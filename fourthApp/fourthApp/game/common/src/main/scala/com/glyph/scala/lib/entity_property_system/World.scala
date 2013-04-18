package com.glyph.scala.lib.entity_property_system

import com.glyph.scala.lib.event.EventManager
import collection.mutable.ListBuffer
import java.util
import com.glyph.libgdx.util.ArrayStack

/**
 * @author glyph
 */
class World {
  val INITIAL_NUMBER_OF_ENTITY = 1024
  val entityFactory = new EntityFactory(this)
  val eventManager = new EventManager
  val systemManager = new SystemManager(this)
  val componentManager = entityFactory.componentManager

  private val entityStack = new ArrayStack[Entity](INITIAL_NUMBER_OF_ENTITY)
  def entities = entityStack
  def addEntity(e:Entity)=entityStack.push(e)
  def removeEntity(e:Entity) = entityStack.remove(e)

  def removeSystem[T<:GameSystem:Manifest](system:T)=systemManager.removeSystem(system)
  def addSystem[T<:GameSystem:Manifest](system:T)=systemManager.addSystem(system)
  def updateSystems(delta:Float)=systemManager.update(delta)
  def componentPool[T<:Component:Manifest]=componentManager.componentPool[T]
  def componentIndex[T<:Component:Manifest]=componentManager.componentIndex[T]
}
