package com.glyph._scala.lib.util.pooling_task

import com.glyph._scala.lib.util.pool.Pooling
import com.glyph._scala.lib.util.Logging
import scala.collection.mutable
import scala.language.reflectiveCalls

object ReflectedPooling extends Logging {

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
  val poolingCache = mutable.HashMap[Class[_], Pooling[_]]()
  implicit def genPooling[T <: {def reset()} : Class]: Pooling[T] =
    poolingCache.getOrElseUpdate(implicitly[Class[T]],
      new Pooling[T] {
        val clazz = implicitly[Class[T]]
        log("generating pooling evidence via reflection for:" + clazz)
        //Inner class cannot be instantiated without a parent class!a
        val constructor = clazz.getConstructor()
        log("success")
        def newInstance: T = constructor.newInstance()
        def reset(tgt: T): Unit = tgt.reset()
      }).asInstanceOf[Pooling[T]]
}