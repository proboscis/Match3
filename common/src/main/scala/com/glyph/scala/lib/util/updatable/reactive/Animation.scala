package com.glyph.scala.lib.util.updatable.reactive

import com.glyph.scala.lib.util.reactive.{Var, Varying}
import com.glyph.scala.lib.util.updatable.task.{Task, TaskProcessor, ParallelProcessor}
import scala.collection.mutable.ListBuffer
import com.glyph.scala.lib.util.updatable.reactive.Animation.Animator
import javax.annotation.processing.Processor

/**
 * @author glyph
 */
trait Animation {
  self: Var[Float] =>
}

object Animation{
  abstract class Animator[T]{
    protected[Animation] var target:Processable[T] = null
    def onStart(t:Processable[T]){
      target = t
    }
    def onFinish(){
      target = null
    }
  }

  val a = new Var(3) with Processor

  implicit class VarProcessable[T](val v:Var[T] with Processor) extends AnyVal with Processable[T]{
    def get: T = v()
    def set(t: T): Unit = v()= t
  }
  trait Processor{
    def add[T](animator:Animator[T]){}
    def remove[T](animator:Animator[T]){}
  }
  trait Processable[T] extends Any{
    def get: T
    def set(t: T)
  }
}


