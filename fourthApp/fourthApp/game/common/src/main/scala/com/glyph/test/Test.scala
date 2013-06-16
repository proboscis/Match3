package com.glyph.test

import collection.mutable.ListBuffer


/**
 * @author glyph
 */
object Test {
  def main(args: Array[String]) {
    //TODO observer test
  }

  class Observable[T<:Any] {
    val observers = ListBuffer[(T) => Unit]()
    def apply(t: T) {
      observers.foreach(_(t))
    }
  }

  trait Observing {
    val blocks = ListBuffer[Any]()
    def observe[T](o: Observable[T])(block: T => Unit) {
      val cb = (t:T)=>{
        block(t)
      }
      blocks += cb
      o.observers += cb
    }
  }

}