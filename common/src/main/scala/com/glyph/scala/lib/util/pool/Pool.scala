package com.glyph.scala.lib.util.pool

import scala.collection.mutable
import com.glyph.scala.lib.util.Logging
import scala.reflect.ClassTag
import com.glyph.scala.lib.util.pool.Pool.PooledAny

trait Pooling[T] {
  def newInstance: T
  def reset(tgt: T)
}

trait Poolable extends Logging{
  self =>
  protected var pool: Pool[self.type] = null

  def setPool(pool: Pool[self.type]) {
    this.pool = pool
  }

  def freeToPool() {
    if (pool != null) {
      log("freed")
      pool.reset(this)
      pool = null
    }else{
      log("Poolable, but the pool is not specified")
    }
  }
}

class Pool[P: Pooling : ClassTag](val max: Int) extends Logging {
  private val pool = mutable.Queue[P]()

  def obtain: P = {
    if (pool.isEmpty) {
      //log("created new instance!:" + implicitly[ClassTag[P]].runtimeClass.getSimpleName)
      implicitly[Pooling[P]].newInstance
    } else {
      pool.dequeue()
    }
  }

  def reset(tgt: P) {
    implicitly[Pooling[P]].reset(tgt)
    if (pool.size < max) {
      pool enqueue tgt
    } else {
      log("warning: this pool reached its max capacity!")
    }
  }

  override def toString: String = "pool" + pool.size

  def foreach(f: P => Unit) {
    val tmp = obtain
    f(tmp)
    reset(tmp)
  }
}

object Pool extends PoolOps {

  class PooledAny[T](val self: T) extends AnyVal {
    def free(implicit pool: Pool[T]) {
      pool.reset(self)
    }
  }

  def apply[T: Pooling : ClassTag](size: Int): Pool[T] = new Pool(size)

  type PoolableType = {def reset()}

  def apply[T <: PoolableType : ClassTag](constructor: () => T, size: Int): Pool[T] = {
    implicit val pooler = new Pooling[T] {
      def newInstance: T = constructor()

      def reset(tgt: T): Unit = tgt.reset()
    }
    new Pool(size)
  }

  def apply[T: ClassTag](constructor: () => T, finalizer: T => Unit, size: Int): Pool[T] = {
    val pooling = new Pooling[T] {
      def newInstance: T = constructor()

      def reset(tgt: T): Unit = finalizer(tgt)
    }
    new Pool(size)(pooling, implicitly[ClassTag[T]])
  }

}

trait PoolOps extends Logging {
  private val DEFAULT_POOL_SIZE = 1000
  private var pools: ClassTag[_] Map Pool[_] = Map() withDefault (_ => null)

  def pool[T: Pool : ClassTag]: Pool[T] = implicitly[Pool[T]]

  def manual[T](implicit pool: Pool[T], tag: ClassTag[T]): T = pool.obtain

  def auto[T <: Poolable](implicit pool: Pool[T], tag: ClassTag[T]): T = {
    val result = pool.obtain
    result.setPool(pool.asInstanceOf[Pool[result.type]])
    result
  }

  implicit def anyToPooled[T](self: T): PooledAny[T] = new PooledAny[T](self)

  implicit def genericPool[T: Pooling : ClassTag]: Pool[T] = {
    val tag = implicitly[ClassTag[T]]
    val result = pools(tag)
    if (result != null) result.asInstanceOf[Pool[T]]
    else {
      val newPool = new Pool[T](DEFAULT_POOL_SIZE)
      pools += tag -> newPool
      log("created a pool for: " + tag.runtimeClass)
      newPool
    }
  }

  def preAlloc[T: Pool : ClassTag]() = 1 to pool[T].max map (_ => manual[T]) foreach (_.free)
}
