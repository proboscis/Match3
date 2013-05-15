package com.glyph.scala.lib.util.modifier

import com.badlogic.gdx.math.Interpolation

/**
 * @author glyph
 */
trait TemporalAction[T] extends TimedAction[T]{
  var interpolation = Interpolation.linear

  override def apply(input: T, delta: Float) {
    if (!complete){
      if (time == 0)onStart(input)
      super.apply(input, delta)
      update(input,interpolation.apply(time/duration))
      if (complete)onEnd(input)
    }
  }
  def update(input:T,alpha:Float)
  def onStart(input:T){}
  def onEnd(input:T){}
}
