package com.glyph._scala.lib.libgdx.actor

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.{Vector3, Rectangle, Matrix4}

/**
 * @author glyph
 */
object ActorUtil {
  val tmp = new Vector3

  def getBounds(camera: Camera)(area: Rectangle)(bound: Rectangle)(transform: Matrix4) {
    tmp.set(area.x, area.y, 0)
    tmp.mul(transform)
    camera.project(tmp)
    bound.x = tmp.x
    bound.y = tmp.y
    tmp.set(area.x + area.width, area.y + area.height, 0)
    tmp.mul(transform)
    camera.project(tmp)
    bound.width = tmp.x - bound.x
    bound.height = tmp.y - bound.y
  }
}
