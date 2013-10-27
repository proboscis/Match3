package com.glyph.scala.lib.util.updatable.reactive

import com.glyph.scala.lib.util.reactive.Var
import com.glyph.scala.lib.util.updatable.task.InterpolationTask

trait Animating[T] extends Any {
  def get: T

  def set(t: T)
}

object Animator {
  implicit def var2animating[T](v: Var[T]): Animating[T] = new Animating[T] {
    def get: T = v()

    def set(t: T): Unit = v() = t
  }

  class IPAnimator(target: Animating[Float]) extends InterpolationTask {
    var start: Float = 0f
    var end: Float = 0f

    override def onStart() {
      super.onStart()
      start = target.get
    }

    def to(to: Float): this.type = {
      end = to
      this
    }

    def apply(alpha: Float) {
      target.set(start + alpha * (end - start))
    }
  }

  def interpolate(target: Animating[Float]): IPAnimator = new IPAnimator(target)
}


