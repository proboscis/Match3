package com.glyph.scala.lib.util.pool

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable

trait Pooling[T]{
  def newInstance:T
  def reset(tgt:T)
}
class Pool[P:Pooling](max:Int){
  private val pool = mutable.Stack[P]()
  def obtain:P = if(pool.isEmpty){
    implicitly[Pooling[P]].newInstance
  } else {
    pool.pop()
  }
  def reset(tgt:P){
    implicitly[Pooling[P]].reset(tgt)
    if(pool.size < max){
      pool push tgt
    }
  }

  override def toString: String = "pool"+pool.size
}
object Pool{
  def apply[T:Pooling](size:Int):Pool[T]=new Pool(size)
  def obtain[T](implicit pool:Pool[T]):T = pool.obtain
  implicit class PooledAny[T](val self:T) extends AnyVal{
    def free(implicit pool: Pool[T]){
      pool.reset(self)
    }
  }
}