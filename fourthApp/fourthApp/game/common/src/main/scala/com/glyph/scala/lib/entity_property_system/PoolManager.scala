package com.glyph.scala.lib.entity_property_system

import collection.mutable.HashMap

/**
 * @author glyph
 */
class PoolManager {
  val poolMap = HashMap.empty[Manifest[_<:Poolable],Pool[_]]
  def getPool[T<:Poolable](implicit typ: Manifest[T]):Pool[T]={
    poolMap get typ match{
      case Some(x)=> x.asInstanceOf[Pool[T]]
      case None => {
        val pool = new Pool[T]
        poolMap(typ) = pool
        pool
      }
    }
  }
}
