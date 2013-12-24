package com.glyph.scala.lib.util.pooling_task

import com.glyph.scala.lib.util.pool.Pooling
import scala.reflect.ClassTag
import com.glyph.scala.lib.util.Logging

object ReflectedPooling extends Logging{
  type RPooling = {
    def reset()
  }


  def genPooling[T <: RPooling : ClassTag]: Pooling[T] = new Pooling[T] {
    val clazz = implicitly[ClassTag[T]]
    val constructor = clazz.runtimeClass.getConstructor()
    log("generated pooling evidence via reflection for:"+clazz.runtimeClass)

    def newInstance: T = constructor.newInstance().asInstanceOf[T]

    def reset(tgt: T): Unit = tgt match {
      case t: RPooling => t.reset()
      case _ => throw new RuntimeException("this class cannot be pooled since it does not have method reset()!")
    }
  }
  private var tagToPooling:ClassTag[_] Map Pooling[_] = Map() withDefault(_=>null)
  implicit def getOrGenPooling[T<:RPooling:ClassTag]:Pooling[T] = {
    val tag = implicitly[ClassTag[T]]
    val result = tagToPooling(tag)
    if(result != null) result.asInstanceOf[Pooling[T]] else{
      val newPooling = genPooling[T]
      tagToPooling += tag->newPooling
      newPooling
    }
  }
}