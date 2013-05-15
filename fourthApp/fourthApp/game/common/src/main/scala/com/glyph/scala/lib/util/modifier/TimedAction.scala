package com.glyph.scala.lib.util.modifier

/**
 * @author glyph
 */
trait TimedAction[T] extends Action[T]{
  val duration:Float
  var time = 0f
  var complete = false

  def apply(input: T, delta: Float) {
    if (!complete){
      time += delta
      complete = time >= duration
    }
  }

  def isComplete: Boolean = complete
}
