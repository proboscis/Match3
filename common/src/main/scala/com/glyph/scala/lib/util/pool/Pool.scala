package com.glyph.scala.lib.util.pool

import scala.collection.mutable
import com.glyph.scala.lib.util.Logging
import scala.reflect.ClassTag
import com.glyph.scala.lib.util.pool.Pool.PooledAny

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

class Pool[P: Pooling](val max: Int,cls:Class[P]) extends Logging {
  def this(m:Int,tag:ClassTag[P]) = this(m,tag.runtimeClass.asInstanceOf[Class[P]])
  log("created a pool for:"+cls.getSimpleName)
  private val pool = new com.badlogic.gdx.utils.Array[P]()

  def manual: P = {
    if (pool.size == 0) {
      val result = implicitly[Pooling[P]].newInstance
      log("created new instance!:" + cls.getSimpleName)
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

  def preAlloc(size:Int){
    1 to size map(_ => manual) foreach reset
  }
  override def toString: String = "pool" + pool.size

  def foreach(f: P => Unit) {
    val tmp = manual
    f(tmp)
    reset(tmp)
  }
}

object Pool {
  def apply[T:Pooling](cls:Class[T],size:Int):Pool[T] = new Pool(size,cls)
  def apply[T: Pooling : ClassTag](size: Int): Pool[T] = new Pool(size,implicitly[ClassTag[T]])
  /*
  def apply[T <: {def reset()} : ClassTag](constructor: () => T)(size: Int): Pool[T] = {
    implicit val pooler = new Pooling[T] {
      def newInstance: T = constructor()

      def reset(tgt: T): Unit = tgt.reset()
    }
    new Pool(size)
  }*/

  def apply[T: ClassTag](constructor: () => T)( finalizer: T => Unit)( size: Int): Pool[T] = {
    val pooling = new Pooling[T] {
      def newInstance: T = constructor()
      def reset(tgt: T): Unit = finalizer(tgt)
    }
    val tag = implicitly[ClassTag[T]]
    new Pool(size,tag)(pooling)
  }

  implicit class PooledAny[T](val self: T) extends AnyVal {
    def free(implicit pool:Pool[T]) = pool.reset(self)
  }
}

trait PoolOps {
  def manual[T: Pool]: T = implicitly[Pool[T]].manual
  def auto[T<:Poolable:Pool]:T = implicitly[Pool[T]].auto
  def preAlloc[T:Pool](size:Int):Unit = implicitly[Pool[T]].preAlloc(size)
  implicit def AnytoPooledAny[T](tgt:T):PooledAny[T] = new PooledAny[T](tgt)
}
object PoolOps extends PoolOps