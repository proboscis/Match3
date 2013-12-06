package com.glyph.scala.lib.util.pooling_task

import com.glyph.scala.lib.util.pool.Pooling
import scala.reflect.ClassTag

object ReflectedPooling {
  implicit def PoolingReflection[T: ClassTag]: Pooling[T] = new Pooling[T] {
    val clazz = implicitly[ClassTag[T]]
    type RPooling = {
      def reset()
    }
    val constructor = clazz.runtimeClass.getConstructor()

    def newInstance: T = constructor.newInstance().asInstanceOf[T]

    def reset(tgt: T): Unit = tgt match {
      case t: RPooling => t.reset()
      case _ => throw new RuntimeException("this class cannot be pooled since it does not have method reset()!")
    }
  }
}