package com.glyph.scala.lib.scalaz

import scalaz._
import Scalaz._
/**
 * @author glyph
 */
object MyScalaz {
  implicit def toFunctor2Ops[T, B[_] : Functor, A[B] : Functor](nested: A[B[T]]): Functor2Ops[T, B, A] = new Functor2Ops[T, B, A] {
    def map2[R](f: (T) => R): A[B[R]] = nested.map(_.map(f))
  }
  trait Functor2Ops[T, B[_], A[B]] {
    def map2[R](f: T => R): A[B[R]]
  }
}
