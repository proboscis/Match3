package com.glyph.scala.lib.util.pool

import scala.collection.mutable

/**
 * @author glyph
 */
object GlobalPool{
  val poolMap = new mutable.HashMap[Class[_],Pool[_]]()
  implicit def globals[T:Pooling](key:Class[T]):Pool[T] = {
    poolMap.getOrElseUpdate(key,new Pool(1000,key)).asInstanceOf[Pool[T]]
  }
}
