package com.glyph._scala.lib.util.updatable.reactive

import com.glyph._scala.lib.util.reactive.{Reactor, Varying}
import com.glyph._scala.lib.util.updatable.Updatable
import scalaz._
import Scalaz._
import com.glyph._scala.lib.util.Logging

/**
 * @author glyph
 */
class Eased[T: Easing](src: Varying[T], interpolation: Float => Float, timeMultiplier: (T, T) => Float)
  extends Reactor
  with Varying[T]
  with Updatable
  with Logging{
  protected var start = src()
  protected var end = src()
  protected var variable = src()
  protected var duration = timeMultiplier(start, end)
  protected var elapsedTime = 0f
  val easing = implicitly[Easing[T]]

  def current: T = variable

  reactVar(src) {
    t => {
      start = current
      end = t
      duration = timeMultiplier(start, end)
      elapsedTime = 0
    }
  }
  override def update(delta: Float) {
    if(elapsedTime > duration){}
    else{
      elapsedTime += delta
      val alpha = if(elapsedTime < duration && elapsedTime+delta > duration) 1
      else interpolation(elapsedTime/duration)
      variable = easing(start, end,alpha)
      notifyObservers(variable)
    }
  }
}

trait Easing[T] {
  def apply(start: T, end: T, alpha: Float): T
}

object Eased {

  implicit object EasingFloat extends Easing[Float] {
    def apply(start: Float, end: Float, alpha: Float): Float = start + (end - start) * alpha
  }

  def apply(src: Varying[Float],interpolation:Float=>Float, time: Float => Float): Eased[Float] = new Eased(src, interpolation,(s:Float,e:Float)=>time(Math.abs(e-s)))
}