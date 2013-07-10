package com.glyph.scala.lib.util.observer.reactive

import collection.immutable.Stack

/**
 * @author glyph
 */
trait Varying[T] extends Reactive[T] {
  self =>
  def current: T

  override def subscribe(callback: (T) => Unit) {
    super.subscribe(callback)
    callback(current)
  }

  def apply(): T = {
    Varying.notify(this)
    current
  }

  //combinator
  def ~[P](v: Varying[P]): Varying[(T, P)] = {
    new Varying[(T, P)] with Reactor {
      def current: (T, P) = (self(), v())

      react(self) {
        s => this.notifyObservers((s, v()))
      }
      react(v) {
        s => this.notifyObservers(self(), s)
      }
    }
  }

  //mapper
  def ->[R](f: (T) => R): Varying[R] = {
    new Varying[R] with Reactor {
      def current: R = f(self())

      react(self) {
        s => this.notifyObservers(f(s))
      }
    }
  }
}

object Varying {
  var tracking = false
  var dependencyMap = Map.empty[Long, Stack[List[Varying[_]]]]

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
}