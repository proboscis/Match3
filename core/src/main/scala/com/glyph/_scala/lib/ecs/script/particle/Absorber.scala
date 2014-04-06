package com.glyph._scala.lib.ecs.script.particle

import com.glyph._scala.lib.ecs.script.{Velocities, Transform, Script}
import com.badlogic.gdx.math.{Vector2, Rectangle}
import com.glyph._scala.lib.ecs.{RemoveChild, AddChild, EntityEvent, Entity}

/**
 * @author glyph
 */
class Absorber extends Script {
  val area = new Rectangle
  val tmp1 = new Vector2
  val tmp2 = new Vector2

  val updater = (pair: (Transform, Velocities)) => {
    pair._1.matrix.getTranslation(tmp2)
    pair._2.acc.add(tmp2.sub(tmp1).scl(-0.016f * 100))
  }

  override def update(delta: Float): Unit = {
    super.update(delta)
    area.getCenter(tmp1)
    val children = entity.children
    children.begin()
    val it = children.iterator()
    area.getCenter(tmp1)
    while (it.hasNext) {
      val child = it.next()
      val transform = child.component[Transform]
      //this is not that slow, huh?
      val body = child.component[Velocities]
      transform.matrix.getTranslation(tmp2)
      body.acc.add(tmp2.sub(tmp1).scl(-delta * 50))
    }
    children.end()
  }

  override def reset(): Unit = {
    super.reset()
    area.set(0, 0, 0, 0)
  }
}
