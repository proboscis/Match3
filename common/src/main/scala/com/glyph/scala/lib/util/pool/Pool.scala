package com.glyph.scala.lib.util.pool

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable
import com.glyph.scala.lib.util.Logging
import scala.reflect.ClassTag

trait Pooling[T]{
  def newInstance:T
  def reset(tgt:T)
}
trait Poolable{
  self =>
  protected var pool:Pool[self.type] = null
  def setPool(pool:Pool[self.type]){
    this.pool = pool
  }
  def freeToPool(){
    if(pool != null){
      pool.reset(this)
      pool = null
    }
  }
}

class Pool[P:Pooling:ClassTag](max:Int)extends Logging{
  private val pool = mutable.Stack[P]()
  def obtain:P = if(pool.isEmpty){
    log("created new instance!:"+implicitly[ClassTag[P]].runtimeClass.getSimpleName)
    implicitly[Pooling[P]].newInstance
  } else {
    pool.pop()
  }
  def reset(tgt:P){
    implicitly[Pooling[P]].reset(tgt)
    if(pool.size < max){
      pool push tgt
    }else{
      log("warning: this pool reached its max capacity!")
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
  def apply[T:Pooling:ClassTag](size:Int):Pool[T]=new Pool(size)
  type PoolableType = {def reset()}
  def apply[T<:PoolableType:ClassTag](constructor: ()=>T,size:Int):Pool[T] = {
    implicit val pooler = new Pooling[T]{
      def newInstance: T = constructor()
      def reset(tgt: T): Unit = tgt.reset()
    }
    new Pool(size)
  }
  def apply[T:ClassTag](constructor:()=>T,finalizer:T=>Unit,size:Int):Pool[T] = {
    val pooling = new Pooling[T] {
      def newInstance: T = constructor()

      def reset(tgt: T): Unit = finalizer(tgt)
    }
    new Pool(size)(pooling,implicitly[ClassTag[T]])
  }
  def pool[T:Pool:ClassTag]:Pool[T] = implicitly[Pool[T]]
  def manual[T](implicit pool:Pool[T],tag:ClassTag[T]):T = pool.obtain
  def auto[T<:Poolable](implicit pool:Pool[T],tag:ClassTag[T]):T = {
    val result = pool.obtain
    result.setPool(pool.asInstanceOf[Pool[result.type]])
    result
  }
  implicit class PooledAny[T](val self:T) extends AnyVal{
    def free(implicit pool: Pool[T]){
      pool.reset(self)
    }
  }
}