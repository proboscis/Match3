package com.glyph.scala.lib.entity_property_system
/**
 * @author glyph
 */
class Pool [T<:Poolable](implicit typ: Manifest[T]){
  val poolables = collection.mutable.Queue.empty[T]
  val runtimeClass = typ.runtimeClass
  def obtain():T={
    if (poolables.isEmpty){
      runtimeClass.newInstance().asInstanceOf[T]
    }else{
      poolables.dequeue()
    }
  }
  def free(e:T){
    e.free()
    poolables.enqueue(e)
  }
}
