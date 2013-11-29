package com.glyph.scala.lib.util.animator

import com.glyph.scala.lib.util.updatable.task.InterpolationTask
import com.glyph.scala.lib.util.reactive.Var
import com.glyph.scala.lib.util.pool.Pooling


/**
 * @author glyph
 */
object Animator {

  //adhoc polymorphismで実装したい。
  //しかし、newしてはならない。
  implicit class Var2Animating[T](val v: Var[T]) extends AnyVal with Animating[T] {
    def get: T = v()

    def set(t: T): Unit = v() = t
  }

  class IPAnimator(var target: Animating[Float]) extends InterpolationTask {
    def this() = this(null)

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

    def set(tgt: Animating[Float]): this.type = {
      target = tgt
      this
    }

    def apply(alpha: Float) {
      target.set(start + alpha * (end - start))
    }

    override def reset() {
      super.reset()
      end = 0
      start = 0
      target = null
    }
  }

  implicit object PoolingIPAnimator extends Pooling[IPAnimator] {
    def newInstance: IPAnimator = new IPAnimator

    def reset(tgt: IPAnimator): Unit = {
      tgt.reset()
    }
  }

  def interpolate(target: Animating[Float]): IPAnimator = new IPAnimator(target)
}
