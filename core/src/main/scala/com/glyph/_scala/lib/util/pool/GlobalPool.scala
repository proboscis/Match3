package com.glyph._scala.lib.util.pool

import scala.collection.mutable

/**
 * @author glyph
 */
object GlobalPool{
  val poolMap = new mutable.HashMap[Class[_],Pool[_]]()
  implicit def globals[T:Pooling:Class]:Pool[T] = {
    poolMap.getOrElseUpdate(implicitly[Class[T]],new Pool(2000)).asInstanceOf[Pool[T]]
  }
}
