package com.glyph.scala.lib.util.pool

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable

trait Pooler[T]{
  def newInstance:T
  def reset(tgt:T)
}
class Pool[P:Pooler](max:Int){
  private val pool = mutable.Stack[P]()
  def obtain:P = if(pool.isEmpty){
    implicitly[Pooler[P]].newInstance
  } else {
    pool.pop()
  }
  def reset(tgt:P){
    implicitly[Pooler[P]].reset(tgt)
    if(pool.size < max){
      pool push tgt
    }
  }
  override def toString: String = "pool"+pool.size
  def foreach(f: P=>Unit){
    val tmp = obtain
    f(tmp)
    reset(tmp)
  }
}
object Pool{
  def apply[T:Pooler](size:Int):Pool[T]=new Pool(size)
  type Poolable = {def reset()}
  def apply[T<:Poolable](constructor: ()=>T)(size:Int):Pool[T] = {
    implicit val pooler = new Pooler[T]{
      def newInstance: T = constructor()
      def reset(tgt: T): Unit = tgt.reset()
    }
    new Pool(size)
  }
  def pool[T:Pool]:Pool[T] = implicitly[Pool[T]]
  def obtain[T](implicit pool:Pool[T]):T = pool.obtain
  implicit class PooledAny[T](val self:T) extends AnyVal{
    def free(implicit pool: Pool[T]){
      pool.reset(self)
    }
  }

}