package com.glyph.scala.lib.util.updatable.vector3

import com.badlogic.gdx.math.Vector3
import com.glyph.scala.lib.util.updatable.task.InterpolationTask

/**
 * @author glyph
 */
trait V3VaryTo extends InterpolationTask{
  val variable:Vector3
  var start:Vector3 = new Vector3
  var end:Vector3 = new Vector3()
  override def onStart() {
    super.onStart()
    start.set(variable)
  }

  def apply(alpha: Float) {
    variable.set(
      (end.x-start.x)*alpha + start.x,
      (end.y-start.y)*alpha + start.y,
      (end.z-start.z)*alpha + start.z
    )
  }
}
