package com.glyph.scala.lib.util.pooling_task

import com.glyph.scala.lib.util.pool.Pooling
import com.glyph.scala.lib.util.updatable.task._
import scala.reflect.ClassTag
import com.glyph.scala.lib.util.updatable.task.tween.Tween
import com.glyph.scala.lib.util.Logging

/**
 * @author glyph
 */
object PoolingTask extends PoolingTaskOps

trait PoolingTaskOps extends Logging {
  var poolingTaskMap: ClassTag[_] Map Pooling[_] = Map() withDefault(_ => null)
  implicit def getPoolingTask[T<:Task:ClassTag]:Pooling[T] = {
    val tag = implicitly[ClassTag[T]]
    val result = poolingTaskMap(tag)
    if(result != null) result.asInstanceOf[Pooling[T]] else{
      val created = new Pooling[T] {
        log("created a pooling task for : "+tag)
        val clazz = implicitly[ClassTag[T]]

        val constructor = clazz.runtimeClass.getConstructor()

        def newInstance: T = constructor.newInstance().asInstanceOf[T]

        def reset(tgt: T): Unit = tgt.reset()
      }
      poolingTaskMap += tag -> created
      created
    }
  }
  /*
  implicit def genPooling[T <: Task : ClassTag]: Pooling[T] = {
    log("created a pooling task for : " + implicitly[ClassTag[T]].runtimeClass)
    new Pooling[T] {
      val clazz = implicitly[ClassTag[T]]

      val constructor = clazz.runtimeClass.getConstructor()

      def newInstance: T = constructor.newInstance().asInstanceOf[T]

      def reset(tgt: T): Unit = tgt.reset()
    }
  }*/

  implicit def poolingInterpolator[T: InterpolatableObject : ClassTag]: Pooling[ObjectInterpolator[T]] = {
    log("created a pooling inetrpolator for : " + implicitly[ClassTag[T]].runtimeClass)
    new Pooling[ObjectInterpolator[T]] {
      def newInstance: ObjectInterpolator[T] = new ObjectInterpolator()

      def reset(tgt: ObjectInterpolator[T]): Unit = tgt.reset()
    }
  }

  val tweenPoolMap = new TagToTypeClassMapGenerator[Tween,Pooling] {
    def create[T:ClassTag]: Pooling[Tween[T]] =  new Pooling[Tween[T]] {
      def newInstance: Tween[T] = new Tween[T]
      def reset(tgt: Tween[T]): Unit = tgt.reset()
    }
  }
  implicit def poolingTween[T:ClassTag] :Pooling[Tween[T]] = tweenPoolMap.apply

}

import scala.reflect.runtime.universe._
abstract class TagToTypeClassMapGenerator[N[_],M[_] <:AnyRef](implicit nTag:ClassTag[N[_]],mTag:ClassTag[M[_]]) extends Logging{
  var map: ClassTag[_] Map AnyRef = Map() withDefault(_=>null.asInstanceOf[AnyRef])
  def create[T:ClassTag]:M[N[T]]
  def apply[T:ClassTag]:M[N[T]] = {
    val tag = implicitly[ClassTag[T]]
    val result = map(tag)
    if(result != null) result.asInstanceOf[M[N[T]]] else{
      val created = create[T]
      log("created a %s[%s[%s]]".format(mTag.runtimeClass.getSimpleName,nTag.runtimeClass.getSimpleName,tag.runtimeClass.getSimpleName))
      map += tag->created
      created
    }
  }
}
