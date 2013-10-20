package com.glyph.scala.lib.util

import scalaz._
import Scalaz._

/**
 * @author glyph
 */
package object reactive {

  /**
   * extractor for tuple
   */
  object ~ {
    def unapply[A, B](t: (A, B)) = Some(t)
  }

  implicit def varyingSemigroup[A: Semigroup]: Semigroup[Varying[A]] = new Semigroup[Varying[A]] {
    def append(f1: Varying[A], f2: => Varying[A]): Varying[A] = new Varying[A] with Reactor {
      def current: A = variable
      var variable = null.asInstanceOf[A]
      reactVar(f1) {
        a1 => variable = a1 |+| f2(); notifyObservers(variable)
      }
      reactVar(f2) {
        a2 => variable = f1() |+| a2; notifyObservers(variable)
      }
    }
  }

  implicit def varyingApply: Apply[Varying] = new Apply[Varying] {
    def ap[A, B](fa: => Varying[A])(f: => Varying[(A) => B]): Varying[B] = new Varying[B] with Reactor {
      def current: B = variable
      var variable = null.asInstanceOf[B]
      reactVar(fa) {
        fa_ => variable = f()(fa_); notifyObservers(variable)
      }
      reactVar(f) {
        f_ => variable = f_(fa()); notifyObservers(variable)
      }
    }
    def map[A, B](fa: Varying[A])(f: (A) => B): Varying[B] = fa map f
  }
}
