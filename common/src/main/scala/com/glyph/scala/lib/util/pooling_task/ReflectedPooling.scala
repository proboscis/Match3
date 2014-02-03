package com.glyph.scala.lib.util.pooling_task

import com.glyph.scala.lib.util.pool.Pooling
import scala.reflect.ClassTag
import com.glyph.scala.lib.util.Logging
import scala.collection.mutable
import scala.language.reflectiveCalls
object ReflectedPooling extends Logging{

  /*
  private var tagToPooling:ClassTag[_] Map Pooling[_] = Map() withDefault(_=>null)
  def getOrGenPooling[T<:RPooling:Class]:Pooling[T] = {
    val tag = implicitly[ClassTag[T]]
    val result = tagToPooling(tag)
    if(result != null) result.asInstanceOf[Pooling[T]] else{
      val newPooling = genPooling[T]
      tagToPooling += tag->newPooling
      newPooling
    }
  }*/
}

trait PoolingOps extends Logging{
  type RPooling = {
    def reset()
  }
  implicit def genPooling[T <: {def reset()} : ClassTag]: Pooling[T] = new Pooling[T] {
    val clazz = implicitly[ClassTag[T]]
    log("generating pooling evidence via reflection for:"+clazz)
    val constructor = clazz.runtimeClass.asInstanceOf[Class[T]].getConstructor()
    log("success")
    def newInstance: T = constructor.newInstance()

    def reset(tgt: T): Unit = tgt match {
      case t: RPooling =>t.reset()
      case _ => throw new RuntimeException("this class cannot be pooled since it does not have method reset()!")
    }
  }
}