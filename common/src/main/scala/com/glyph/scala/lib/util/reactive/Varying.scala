package com.glyph.scala.lib.util.reactive


/**
 * @author glyph
 */
trait Varying[@specialized(Float,Int) T] extends Reactive[T] {
  self =>
  def current: T

  override def subscribe(callback: (T) => Unit) {
    super.subscribe(callback)
    callback(current)
  }

  def * : Varying[T] = self

  def apply(): T = {
    current
  }

  //combinator
  def ~[@specialized(Float,Int)P](v: Varying[P]): Varying[(T, P)] = new Paired(self,v)

  /**
   * mapper
   */
  def map[R](f: (T) => R): Varying[R] = new Mapped(self, f)

  /*
  def flatMap[R](f: T => Varying[R]): Varying[R] = f(current) match {
    case noneMapped =>
      new Varying[R] with Reactor {
        var variable = null.asInstanceOf[R]

        def current: R = variable

        reactVar(self) {
          a => variable = f(a)(); println("self update=>" + a + ":" + variable); this.notifyObservers(variable)
        }
        reactVar(noneMapped) {
          a => variable = f(self.current)(); println("instance update =>" + a + ":" + variable); notifyObservers(variable)
        }
      }
  }*/


  //maps to event source
  def toEvents: EventSource[T] = {
    var prev = current
    new EventSource[T] with Reactor {
      reactVar(self) {
        s => if (prev != current) emit(current); prev = current
      }
    }
  }

  def foreach(f: T => Unit) {
    f(current)
  }

  override def toString: String = "<" + current + ">" + super.toString

}

class Mapped[@specialized(Float,Int) T, @specialized(Float,Int) R](val self: Varying[T], val mapping: T => R) extends Varying[R] with Reactor {
  var variable: R = null.asInstanceOf[R]

  def current: R = variable

  reactVar(self) {
    s => variable = mapping(s); this.notifyObservers(variable)
  }
}

class Paired[@specialized(Float,Int)A,@specialized(Float,Int)B](a:Varying[A],b:Varying[B]) extends Varying[(A,B)] with Reactor{
  var variable = (a(),b())
  def current: (A, B) = variable
  reactVar(a){
    s => {
      variable = (s,b())
      notifyObservers(variable)
    }
  }
  reactVar(b){
    s => {
      variable = (a(),s)
      notifyObservers(variable)
    }
  }
}
/*
 new Varying[(T, P)] with Reactor {
      var variable:(T,P) = (self(), v())

      def current: (T, P) = variable

      reactVar(self) {
        s => {
          variable = (s, v())
          this.notifyObservers(variable)
        }
      }
      reactVar(v) {
        s => {
          variable = (self(), s)
          this.notifyObservers(variable)
        }
      }
    }
 */

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