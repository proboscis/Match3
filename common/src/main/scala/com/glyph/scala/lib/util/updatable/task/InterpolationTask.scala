package com.glyph.scala.lib.util.updatable.task

import com.badlogic.gdx.math.Interpolation

/**
 * @author glyph
 */
trait InterpolationTask extends TimedTask {
  var interpolation = Interpolation.linear
  override def update(delta: Float) {
    if (!completed) {
      super.update(delta)
      if (!completed) {
        this.apply(interpolation(time / duration))
      } else {
        this.apply(interpolation(1))
      }
    }
  }

  def apply(alpha: Float)

  def using(i: Interpolation): this.type = {
    interpolation = i
    this
  }
}
