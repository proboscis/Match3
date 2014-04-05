package com.glyph._scala.lib.ecs

import com.badlogic.gdx.utils.DelayedRemovalArray
import com.glyph._scala.lib.ecs.system.EntitySystem

/**
 * @author glyph
 */
class Scene {
  val objects = new DelayedRemovalArray[Entity]()
  val systemMap = new com.badlogic.gdx.utils.ObjectMap[Class[_], EntitySystem](20)
  val systemList = new com.badlogic.gdx.utils.Array[EntitySystem](20)

  def +=(ent: Entity) {
    objects.add(ent)
    ent.setScene(this)
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