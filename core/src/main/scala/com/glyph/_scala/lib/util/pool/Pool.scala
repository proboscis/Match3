package com.glyph._scala.lib.util.pool

import com.glyph._scala.lib.util.Logging
import com.glyph._scala.lib.util.pool.Pool.PooledAny

trait Pooling[T] {
  def newInstance: T

  def reset(tgt: T)
}

trait Poolable extends Logging {
  self =>
  protected var pool: Pool[self.type] = null

  def setPool(pool: Pool[_]) {
    this.pool = pool.asInstanceOf[Pool[self.type]]
  }

  def freeToPool() {
    if (pool != null) {
      pool.reset(this)
      pool = null
    } else {
      log("Poolable, but the pool is not specified" + this.getClass)
    }
  }
}

class Pool[P: Pooling : Class](val max: Int) extends Logging {
  log("created a pool for:" + implicitly[Class[P]])
  private val pool = new com.badlogic.gdx.utils.Array[P]()

  def manual: P = {
    if (pool.size == 0) {
      val result = implicitly[Pooling[P]].newInstance
      implicitly[Pooling[P]].reset(result)
      //log("created new instance!:" + implicitly[Class[P]].getSimpleName)
      result
    } else {
      pool.pop()
    }
  }

  def auto: P = {
    val result = manual
    assert(result.isInstanceOf[Poolable])
    result.asInstanceOf[Poolable].setPool(this)
    result
  }

  def reset(tgt: P) {
    implicitly[Pooling[P]].reset(tgt)
    if (pool.size < max) {
      pool add tgt
      //log("freed "+tgt.getClass)
    } else {
      log("warning: this pool reached its max capacity!" + tgt.getClass)
    }
  }

  def preAlloc(size: Int) {
    1 to size map (_ => manual) foreach reset
  }

  override def toString: String = "pool" + pool.size

  def foreach(f: P => Unit) {
    val tmp = manual
    f(tmp)
    reset(tmp)
  }
}

object Pool {
  def apply[T: Pooling : Class](size: Int): Pool[T] = new Pool(size)

  /*
  def apply[T <: {def reset()} : ClassTag](constructor: () => T)(size: Int): Pool[T] = {
    implicit val pooler = new Pooling[T] {
      def newInstance: T = constructor()

      def reset(tgt: T): Unit = tgt.reset()
    }
    new Pool(size)
  }*/

  def apply[T: Class](constructor: () => T)(finalizer: T => Unit)(size: Int): Pool[T] = {
    val pooling = new Pooling[T] {
      def newInstance: T = constructor()

      def reset(tgt: T): Unit = finalizer(tgt)
    }
    new Pool[T](size)(pooling, implicitly[Class[T]])
  }

  implicit class PooledAny[T](val self: T) extends AnyVal {
    def free(implicit pool: Pool[T]) = pool.reset(self)
  }

}

trait PoolOps {

  import scala.language.implicitConversions

  def manual[T: Pool]: T = implicitly[Pool[T]].manual

  def auto[T <: Poolable : Pool]: T = implicitly[Pool[T]].auto

  def preAlloc[T: Pool](size: Int): Unit = implicitly[Pool[T]].preAlloc(size)

  implicit def AnyToPooledAny[T](tgt: T): PooledAny[T] = new PooledAny[T](tgt)
}

object PoolOps extends PoolOps