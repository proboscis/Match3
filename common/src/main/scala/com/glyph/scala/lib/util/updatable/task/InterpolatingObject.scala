package com.glyph.scala.lib.util.updatable.task

import com.glyph.scala.lib.util.pool.Pooling

/**
 * @author glyph
 */
object InterpolatingObject {

  trait AnimatedObject[T] {
    def set(tgt: T)(v: T)
  }

  trait InterpolatableObject[T] extends AnimatedObject[T] with Pooling[T] {
    def interpolateAndSet(target: T, start: T, end: T, alpha: Float)
  }

  class ObjectInterpolator[T: InterpolatableObject](var target: T) extends InterpolationTask {
    def this() = this(null.asInstanceOf[T])

    val impl = implicitly[InterpolatableObject[T]]
    var start: T = impl.newInstance
    var end: T = impl.newInstance

    override def onStart() {
      super.onStart()
      impl.set(start)(target)
    }

    def set(target: T, end: T) {
      this.target = target
      impl.set(this.end)(end)
    }

    def apply(alpha: Float): Unit = impl.interpolateAndSet(target, start, end, alpha)

    override def reset() {
      super.reset()
      impl.reset(end)
      impl.reset(start)
      target = null.asInstanceOf[T]
    }
  }

  implicit def poolingInterpolator[T: InterpolatableObject]: Pooling[ObjectInterpolator[T]] = new Pooling[ObjectInterpolator[T]] {
    def newInstance: ObjectInterpolator[T] = new ObjectInterpolator()

    def reset(tgt: ObjectInterpolator[T]): Unit = tgt.reset()
  }
}
