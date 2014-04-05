package com.glyph._scala.lib.ecs.script

import com.badlogic.gdx.math.Vector2
import com.glyph._scala.lib.ecs.Entity
import com.glyph.ClassMacro._
/**
 * @author glyph
 */
class SimplePhysics extends Script{
  val acc = new Vector2
  val vel = new Vector2()
  var transform:Transform = null


  override def initialize(self: Entity): Unit = {
    super.initialize(self)
    transform = self.getScript[Transform]
  }

  override def update(delta: Float): Unit = {
    super.update(delta)
    vel.add(acc.scl(delta))
    transform.matrix.translate(acc.set(vel).scl(delta))
    acc.set(0f,0f)
  }

  override def reset(): Unit = {
    super.reset()
    acc.set(0,0)
    vel.set(0,0)
    transform = null
  }
}
