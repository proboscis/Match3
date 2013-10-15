package com.glyph.scala.lib.util.reactive

import com.glyph.scala.lib.util.lifting.Variable


/**
 * @author glyph
 */
trait Varying[T] extends Reactive[T]{
  self =>
  def current: T

  override def subscribe(callback: (T) => Unit) {
    super.subscribe(callback)
    callback(current)
  }

  def apply(): T = {
    //Varying.notify(this)
    current
  }

  //combinator
  def ~[P](v: Varying[P]): Varying[(T, P)] = {
    new Varying[(T, P)] with Reactor {
      def current: (T, P) = (self(), v())
      reactVar(self) {
        s => this.notifyObservers((s, v()))
      }
      reactVar(v) {
        s => this.notifyObservers(self(), s)
      }
    }
  }

  /**
   * mapper
   */
  def map[R](f: (T) => R): Varying[R] = {
    new Varying[R] with Reactor {
      var variable:R = null.asInstanceOf[R]
      reactVar(self) {
        s => variable = f(s);this.notifyObservers(variable)
      }
      def current: R = variable
    }
  }

  //maps to event source
  def toEvents: EventSource[T] = {
    new EventSource[T] with Reactor {
      reactVar(self) {
        s => emit(s)
      }
    }
  }

  override def toString: String ="<"+current+">"+super.toString

}
/*
object Varying {
  var tracking = false
  var dependencyMap = Map.empty[Long, Stack[List[Varying[_]]]]

  @Deprecated
  def notify(r: Varying[_]) {
    if (tracking) {
      //prevent checking map when not necessary
      val id = Thread.currentThread().getId
      dependencyMap get id match {
        case Some(depStack) => dependencyMap = dependencyMap + (id -> depStack.pop.push(r :: depStack.head))
        case None => //do nothing
      }
    }
  }

  @Deprecated
  def getDependency(block: => Unit): List[Varying[_]] = {
    val id = Thread.currentThread().getId
    tracking = true
    val depStack1 = dependencyMap.getOrElse(id, Stack.empty[List[Varying[_]]])
    dependencyMap = dependencyMap + (id -> depStack1.push(Nil))
    block //side Effect!
    val depStack = dependencyMap(id)
    dependencyMap = dependencyMap + (id -> depStack.pop)
    //make sure no one else is tracking.
    if (dependencyMap.values.forall(_.isEmpty)) tracking = false
    depStack.head.distinct
  }
}*/