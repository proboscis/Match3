package com.glyph._scala.lib.ecs.component

import com.badlogic.gdx.math.Vector2
import com.glyph._scala.lib.ecs.{Component, IsComponent, Entity}
import com.glyph._scala.lib.ecs.script.Script

/**
 * @author glyph
 */
class Velocities extends Component{
  val force = new Vector2
  val vel = new Vector2()
  var ignoreGravity = false
  var friction = 0f
  var viscosity = 0f
  var weight = 0f
  def reset(): Unit = {
    force.set(0,0)
    vel.set(0,0)
    friction = 0f
    weight = 1f
    viscosity = 0f
    ignoreGravity = false
  }
}
object Velocities{
  implicit object spIsComponent extends IsComponent[Velocities]
}
class SimplePhysics extends Script{

  val tmp = new Vector2
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
    val force = sp.force
    val friction = sp.friction
    val viscosity = sp.viscosity
    if(friction != 0f) force.add(tmp.set(vel).scl(-1 * friction))
    if(viscosity != 0f) {
      val p = tmp.set(vel).len2
      tmp.nor().scl(-p * viscosity)
      force.add(tmp)
    }
    vel.add(force.scl(delta/sp.weight))
    entity.component[Transform].matrix.translate(tmp.set(vel).scl(delta))
    force.set(0f,0f)
  }

  override def reset(): Unit = {
    super.reset()
    sp = null
    tr = null
  }
}