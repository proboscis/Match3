package com.glyph.scala.lib.util.pooling_task

import com.glyph.scala.lib.util.pool.Pooling
import com.glyph.scala.lib.util.updatable.task._
import scala.reflect.ClassTag

/**
 * @author glyph
 */
object PoolingTask extends PoolingTaskOps

trait PoolingTaskOps {
  implicit def genPooling[T <: Task : ClassTag]: Pooling[T] = new Pooling[T] {
    val clazz = implicitly[ClassTag[T]]

    val constructor = clazz.runtimeClass.getConstructor()

    def newInstance: T = constructor.newInstance().asInstanceOf[T]

    def reset(tgt: T): Unit = tgt.reset()
  }

  implicit def poolingInterpolator[T: InterpolatableObject]: Pooling[ObjectInterpolator[T]] = new Pooling[ObjectInterpolator[T]] {
    def newInstance: ObjectInterpolator[T] = new ObjectInterpolator()

    def reset(tgt: ObjectInterpolator[T]): Unit = tgt.reset()
  }
}
