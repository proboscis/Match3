package com.glyph.scala.lib.util.updatable.reactive

import com.glyph.scala.lib.util.updatable.task.{InterpolationTask, ParallelProcessor}
import com.badlogic.gdx.math.Interpolation
import com.glyph.scala.lib.util.reactive
import reactive.{Varying, Reactor}

/**
 * @author glyph
 */
class DeprecatedEasing(processor: ParallelProcessor)(private var variable: Float)(durationFunc:(Float)=> Float)(interpolation: Interpolation) extends Varying[Float] {
  var easier: Easier = null

  def current: Float = variable
  def current_=(f:Float){
    //println("set:"+f)
    variable = f
    notifyObservers(current)
  }
  def update(v: Float) {
    if (easier != null) processor.cancel(easier)
    easier = new Easier(current, v).using(interpolation).for_(durationFunc(Math.abs(current-v)))
    processor.add(easier)
  }

  class Easier(start: Float, end: Float) extends InterpolationTask {
    def apply(alpha: Float) {
      //println("alpha:"+alpha)
      current = interpolation.apply(start, end, alpha)
      //notifyObservers(variable)
    }
  }
}

object DeprecatedEasing {
  def apply(processor: ParallelProcessor)(v: Varying[Float])(durationFunc:(Float)=> Float,initial:Float = v())(interpolation: Interpolation = Interpolation.linear): DeprecatedEasing = {
    new DeprecatedEasing(processor)(initial)(durationFunc)(interpolation) with Reactor {
      reactVar(v)(update)
    }
  }
}
