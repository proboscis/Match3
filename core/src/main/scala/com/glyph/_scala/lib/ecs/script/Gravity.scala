package com.glyph._scala.lib.ecs.script

import com.badlogic.gdx.math.Vector2
import com.glyph._scala.lib.ecs.AddChild
import com.glyph._scala.lib.ecs.component.{Velocities, Transform}

/**
 * causes any children of attached entity to move towards center with specified power.
 * @author glyph
 */
class Gravity extends Script {
  val center = new Vector2
  var power:Float = 0f
  val tmp2 = new Vector2
  override def update(delta: Float): Unit = {
    super.update(delta)
    val children = entity.children
    children.begin()
    val it = children.iterator()
    while (it.hasNext) {
      val child = it.next()
      val transform = child.component[Transform]
      //this is not that slow, huh?
      val body = child.component[Velocities]
      if(transform != null && body != null && !body.ignoreGravity){
        transform.matrix.getTranslation(tmp2)
        body.force.add(tmp2.sub(center).scl(-delta * power * body.weight))
      }
    }
    children.end()
  }

  override def reset(): Unit = {
    super.reset()
    center.set(0,0)
    power = 0f
  }
}
