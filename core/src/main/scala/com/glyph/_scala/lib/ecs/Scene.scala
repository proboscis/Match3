package com.glyph._scala.lib.ecs

import com.badlogic.gdx.utils.{ObjectIntMap, DelayedRemovalArray}
import com.glyph._scala.lib.ecs.system.EntitySystem
import com.glyph._scala.lib.util.pool.GlobalPool._
import com.glyph._scala.game.Glyphs
import Glyphs._
/**
 * @author glyph
 */
class Scene {
  //TODO you must invalidate the scene before switching to next scene.
  var isDisposed = false
  val objects = new DelayedRemovalArray[Entity]()
  val systemMap = new com.badlogic.gdx.utils.ObjectMap[Class[_], EntitySystem](20)
  val systemList = new com.badlogic.gdx.utils.Array[EntitySystem](20)
  var nextComponentId = 0
  private val componentIdMap = new ObjectIntMap[IsComponent[_]]()
  def createEntity():Entity = {
    val e = auto[Entity]
    e.setScene(this)
    e
  }
  def setOrRegisterComponentId(ev:IsComponent[_]){
    if(ev.scene != this){
      ev.scene = this
      ev.componentId = nextComponentId
      if(!componentIdMap.containsKey(ev)){
        componentIdMap.put(ev,nextComponentId)
      }else{
        throw new IllegalStateException("this is definitely wrong. WTF!?")
      }
      nextComponentId+=1
    }
  }

  def +=(ent: Entity) {
    objects.add(ent)
  }

  def -=(ent: Entity) {
    objects.removeValue(ent, true)
    ent.freeToPool()
  }

  def getSystem[T<:EntitySystem : Class] = systemMap.get(implicitly[Class[T]]).asInstanceOf[T]

  def +=(system: EntitySystem) = {
    systemMap.put(system.getClass, system)
    systemList.add(system)
  }

  //TODO implement removal of system
  def update(delta: Float) {
    objects.begin()
    val it1 = objects.iterator()
    while (it1.hasNext) {
      it1.next().updateScripts(delta)
    }
    objects.end()
    val it = systemList.iterator()
    while (it.hasNext) {
      it.next().update(this, delta)
    }
  }

  def draw() {
    val it = systemList.iterator()
    while (it.hasNext) {
      it.next().draw(this)
    }
  }
}