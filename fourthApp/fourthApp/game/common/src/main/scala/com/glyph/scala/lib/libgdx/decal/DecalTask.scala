package com.glyph.scala.lib.libgdx.decal

import com.badlogic.gdx.math.Vector3
import com.glyph.scala.lib.util.updatable.task.Task
import com.badlogic.gdx.graphics.g3d.decals.Decal

/**
 * @author glyph
 */
trait DecalTask extends Task {
  val decal: Decal
}

object DecalTask {

  trait MoveTo extends DecalInterpolation {
    var start: Vector3 = new Vector3
    var end: Vector3 = new Vector3()

    override def onStart() {
      super.onStart()
      start.set(decal.getPosition)
    }

    def apply(alpha: Float) {
      decal.setPosition(
        (end.x - start.x) * alpha + start.x,
        (end.y - start.y) * alpha + start.y,
        (end.z - start.z) * alpha + start.z
      )
    }

  }
}
