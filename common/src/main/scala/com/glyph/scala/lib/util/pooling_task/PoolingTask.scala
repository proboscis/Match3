package com.glyph.scala.lib.util.pooling_task

import com.glyph.scala.lib.util.pool.Pooling
import com.glyph.scala.lib.util.updatable.task._
import scala.reflect.ClassTag
import com.glyph.scala.lib.util.updatable.task.tween.Tween
import com.glyph.scala.lib.util.Logging

/**
 * @author glyph
 */
object PoolingTask extends PoolingTaskOps

trait PoolingTaskOps extends Logging {
  implicit def genPooling[T <: Task : ClassTag]: Pooling[T] = {
    log("created a pooling task for : " + implicitly[ClassTag[T]].runtimeClass)
    new Pooling[T] {
      val clazz = implicitly[ClassTag[T]]

      val constructor = clazz.runtimeClass.getConstructor()

      def newInstance: T = constructor.newInstance().asInstanceOf[T]

      def reset(tgt: T): Unit = tgt.reset()
    }
  }

  implicit def poolingInterpolator[T: InterpolatableObject : ClassTag]: Pooling[ObjectInterpolator[T]] = {
    log("created a pooling inetrpolator for : " + implicitly[ClassTag[T]].runtimeClass)
    new Pooling[ObjectInterpolator[T]] {
      def newInstance: ObjectInterpolator[T] = new ObjectInterpolator()

      def reset(tgt: ObjectInterpolator[T]): Unit = tgt.reset()
    }
  }

  implicit def poolingTween[T: ClassTag]: Pooling[Tween[T]] = {
    log("created a pooling tween for : " + implicitly[ClassTag[T]].runtimeClass)
    new Pooling[Tween[T]] {
      def newInstance: Tween[T] = new Tween[T]

      def reset(tgt: Tween[T]): Unit = tgt.reset()
    }
  }
}
