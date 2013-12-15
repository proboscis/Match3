package com.glyph.scala.lib.libgdx.conversion

import com.badlogic.gdx.graphics.Color
import com.glyph.scala.lib.util.updatable.task.InterpolatingObject.InterpolatableObject

/**
 * @author glyph
 */
object InterpolatableGdx extends InterpolatableGdxOps
trait InterpolatableGdxOps {
  implicit object InterpolatedColor extends InterpolatableObject[Color] {
    def set(tgt: Color)(v: Color): Unit = tgt.set(v)
    def newInstance: Color = new Color()
    def interpolateAndSet(target: Color, start: Color, end: Color, alpha: Float) {
      target.a = start.a + (end.a - start.a) * alpha
      target.r = start.r + (end.r - start.r) * alpha
      target.g = start.g + (end.g - start.g) * alpha
      target.b = start.b + (end.b - start.b) * alpha
    }
    def reset(tgt: Color): Unit = tgt.set(1, 1, 1, 1)
  }
}
