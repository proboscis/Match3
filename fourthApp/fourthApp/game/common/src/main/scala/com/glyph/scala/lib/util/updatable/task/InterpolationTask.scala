package com.glyph.scala.lib.util.updatable.task

import com.badlogic.gdx.math.Interpolation
import com.glyph.scala.lib.util.updatable.task.TimedTask

/**
 * @author glyph
 */
trait InterpolationTask extends TimedTask{
  var interpolation = Interpolation.linear

  override def update(delta: Float) {
    if (!completed){
      if (time == 0)onStart()
      super.update(delta)
      if (!completed){
        this.apply(interpolation(time/duration))
      }else{
        this.apply(interpolation(1))
      }
      if (completed)onEnd()
    }
  }
  def apply(alpha:Float)
  def onStart(){}
  def onEnd(){}
  def using(i:Interpolation):this.type={
    interpolation = i
    this
  }
}
