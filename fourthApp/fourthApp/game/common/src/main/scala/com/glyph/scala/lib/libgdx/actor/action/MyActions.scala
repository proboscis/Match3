package com.glyph.scala.lib.libgdx.actor.action

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import com.badlogic.gdx.math.{MathUtils, Interpolation}
import com.badlogic.gdx.scenes.scene2d.Action

/**
 * @author glyph
 */
object MyActions {

  import Interpolation._
  import MathUtils._

  def jump(height: Float, duration: Float) = new TemporalAction(duration, linear) {
    var x, y = 0f

    override def begin() {
      super.begin()
      x = actor.getX
      y = actor.getY
    }

    def update(a: Float) {
      actor.setPosition(x, y + Math.abs(sin(a * PI2) * height) / (a + 1))
    }

    override def end() {
      super.end()
      actor.setPosition(x, y)
    }
  }
  object NullAction extends Action{
    def act(p1: Float): Boolean = true
  }
}
