package com.glyph._scala.lib.ecs.script

import com.badlogic.gdx.math.Vector2
import com.glyph._scala.lib.ecs.{Component, IsComponent, Entity}
import com.glyph.ClassMacro._
import com.glyph._scala.lib.util.pool.Poolable

/**
 * @author glyph
 */
class Velocities extends Component{
  val acc = new Vector2
  val vel = new Vector2()
  def reset(): Unit = {
    acc.set(0,0)
    vel.set(0,0)
  }
}
object Velocities{
  implicit object spIsComponent extends IsComponent[Velocities]
}
class SimplePhysics extends Script{
  var sp:Velocities = null
  var tr:Transform = null
  override def initialize(self: Entity): Unit = {
    super.initialize(self)
    tr = self.component[Transform]
    sp = self.component[Velocities]
  }

  override def update(delta: Float): Unit = {
    super.update(delta)
    //for fast access
    val vel = sp.vel
    val acc = sp.acc
    vel.add(acc.scl(delta))
    tr.matrix.translate(acc.set(vel).scl(delta))
    acc.set(0f,0f)
  }

  override def reset(): Unit = {
    super.reset()
    sp = null
    tr = null
  }
}