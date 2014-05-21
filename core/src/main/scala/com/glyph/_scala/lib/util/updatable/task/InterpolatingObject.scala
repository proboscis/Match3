package com.glyph._scala.lib.util.updatable.task

import com.glyph._scala.lib.util.Logging
import com.glyph._scala.game.Glyphs
import com.badlogic.gdx.graphics.Color

trait Accessor[T] {
  def size: Int
  def get(tgt: T, values: Array[Float])
  def set(tgt: T, values: Array[Float])
}
object Accessors{
  object Color extends Accessor[Color]{
    def size: Int = 4
    def get(tgt: Color, values: Array[Float]): Unit = {
      values(0) = tgt.r
      values(1) = tgt.g
      values(2) = tgt.b
      values(3) = tgt.a
    }

    def set(tgt: Color, values: Array[Float]): Unit = {
      tgt.r = values(0)
      tgt.g = values(1)
      tgt.b = values(2)
      tgt.a = values(3)
    }
  }
}

class Interpolator[T] extends InterpolationTask with AutoFree with Logging {
  var start = new Array[Float](4)
  var end = new Array[Float](4)
  val values = new Array[Float](4)
  var target: T = null.asInstanceOf[T]
  var accessor: Accessor[T] = null.asInstanceOf[Accessor[T]]
  var size = 0

  override def onStart() {
    super.onStart()
    size = accessor.size
    accessor.get(target, start)
  }

  def set(target: T): this.type = {
    this.target = target
    this
  }

  def of(a: Accessor[T]): this.type = {
    this.accessor = a
    this
  }

  def to(x:Float):this.type = {
    end(0) = x
    this
  }
  def to(x:Float,y:Float):this.type={
    end(0) = x
    end(1) = y
    this
  }
  def to(x:Float,y:Float,z:Float):this.type = {
    end(0) = x
    end(1) = y
    end(2) = z
    this
  }
  def to(x:Float,y:Float,z:Float,w:Float):this.type = {
    end(0) = x
    end(1) = y
    end(2) = z
    end(3) = w
    this
  }

  def apply(alpha: Float) {
    var i = 0
    val v = values
    val s = start
    val e = end
    while (i < size) {
      val si = s(i)
      v(i) = si + (e(i) - si) * alpha
      i += 1
    }
    accessor.set(target, v)
  }
  override def reset() {
    super.reset()
    target = null.asInstanceOf[T]
    accessor = null.asInstanceOf[Accessor[T]]
    size = 0
  }
}
object Interpolate{
  import com.glyph._scala.lib.util.pool.GlobalPool.globals
  import Glyphs._
  def apply[T](target:T):Interpolator[T] = auto[Interpolator[AnyRef]].asInstanceOf[Interpolator[T]] set target
}
