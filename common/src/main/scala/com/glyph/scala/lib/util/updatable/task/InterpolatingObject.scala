package com.glyph.scala.lib.util.updatable.task

import com.glyph.scala.lib.util.pool.Pooling
import com.glyph.scala.lib.libgdx.conversion.AnimatingGdx.Animated

/**
 * @author glyph
 */
object InterpolatingObject {
  trait AnimatedObject[T]{
    def set(tgt:T)(v:T)
    def get(tgt:T):T
  }
  trait InterpolatableObject[T] extends AnimatedObject[T]{
    def interpolatedValue(target:T,start:T,end:T,alpha:Float):T
  }
  class ObjectInterpolator[T:Interpolatable:Animated](var target:T) extends InterpolationTask{
    def this() = this(null.asInstanceOf[T])
    var start:T = null.asInstanceOf[T]
    var end:T = null.asInstanceOf[T]
    override def onStart(){
      super.onStart()
      start = implicitly[Animated[T]].get(target)
    }
    def set(target:T,end:T){
      this.target = target
      this.end = end
    }
    def apply(alpha: Float): Unit = implicitly[Animated[T]].set(target)(implicitly[Interpolatable[T]].interpolatedValue(start,end,alpha))
    override def reset(){
      super.reset()
      end = null.asInstanceOf[T]
      start = null.asInstanceOf[T]
      target = null.asInstanceOf[T]
    }
  }
  implicit def poolingInterpolator[T:Interpolatable:Animated]:Pooling[ObjectInterpolator[T]] = new Pooling[ObjectInterpolator[T]]{
    def newInstance: ObjectInterpolator[T] = new ObjectInterpolator()
    def reset(tgt: ObjectInterpolator[T]): Unit = tgt.reset()
  }
}
