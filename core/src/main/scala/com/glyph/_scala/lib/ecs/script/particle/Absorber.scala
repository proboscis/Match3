package com.glyph._scala.lib.ecs.script.particle

import com.glyph._scala.lib.ecs.script.{SimplePhysics, Transform, Script}
import com.badlogic.gdx.math.{Vector2, Rectangle}
import com.glyph._scala.lib.ecs.{RemoveChild, AddChild, EntityEvent, Entity}
import scala.collection.mutable.ArrayBuffer
import com.glyph.ClassMacro._
import com.badlogic.gdx.physics.box2d.Body
import scala.collection.immutable.HashMap
import scala.collection.parallel.mutable

/**
 * @author glyph
 */
class Absorber extends Script{
  val area = new Rectangle
  val tmp1 = new Vector2
  val tmp2 = new Vector2
  val cache = new collection.mutable.HashMap[Entity,(Transform,SimplePhysics)]()
  val listener = (e:EntityEvent)=>{
    e match {
      case AddChild(c) => cache(c) = (c.getScript[Transform],c.getScript[SimplePhysics])
      case RemoveChild(c) => cache.remove(c)
      case _=> //
    }
  }:Unit
  override def initialize(self: Entity): Unit = {
    super.initialize(self)
    self.addModificationListener(listener)
  }
  val updater = (pair:(Transform,SimplePhysics))=>{
    pair._1.matrix.getTranslation(tmp2)
    pair._2.acc.add(tmp2.sub(tmp1).scl(- 0.016f*100))
  }
  override def update(delta: Float): Unit = {
    super.update(delta)
    area.getCenter(tmp1)
    cache.values foreach updater
    /*
    val children = entity.children
    children.begin()
    val it = children.iterator()
    area.getCenter(tmp1)
    while(it.hasNext){
      val child = it.next()
      val transform = child.getScript[Transform]
      //this is not that slow, huh?
      val body = child.getScript[SimplePhysics]
     transform.matrix.getTranslation(tmp2)
        body.acc.add(tmp2.sub(tmp1).scl(-delta*50))
    }
    children.end
    */
  }
  override def reset(): Unit = {
    if(entity != null)    entity.removeModificationListener(listener)
    super.reset()
    area.set(0,0,0,0)
  }
}
