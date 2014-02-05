package com.glyph._scala.lib.util.reactive

import scala.annotation.tailrec

/**
 * @author glyph
 */
class Divider[T: Numeric : Manifest](val limit: Varying[T], initialCount: Int = 0) extends Varying[Int] with Reactor {
  private val evidence = implicitly[Numeric[T]]

  import evidence._

  val source = Var(evidence.zero, "Divider:source")
  private val count = Var(initialCount, "Divider:count")
  reactVar(source) {
    s =>
      val lim = limit()
      @tailrec
      def loop(n: T, i: Int) {
        if (gteq(n, lim) && gt(lim,zero)) {
          println("n>lim:"+n+","+lim)
          loop(minus(n, lim), i + 1)
        } else {
          if(i != 0) count() += i
          if(source() != n)source() = n
        }
      }
      loop(s, 0)
  }
  reactVar(count) {
    c => notifyObservers(c)
  }

  def current: Int = count()

  def <=(exp: T) {
    source() = plus(source(), exp)
  }

  def -=(p: Int) {
    count() -= p
  }
}
