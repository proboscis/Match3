package com.glyph._scala.lib.util.reactive

import scala.concurrent.{Future, ExecutionContext}
import scala.util.Try
import scalaz.Functor
import com.glyph._scala.lib.libgdx.GdxUtil
import com.glyph._scala.lib.util.Logging

/**
 * @author glyph
 */

trait VaryingOps {

  implicit class VaryingFuture[T](tgt: Varying[T]) {
    def mapFuture[U](f: T => U)(implicit c: ExecutionContext): Varying[Option[Try[U]]] = new Varying[Option[Try[U]]] with Reactor with Logging{
      var variable: Option[Try[U]] = None
      var prevCallback = null.asInstanceOf[Cancellable[U]]
      var future = null.asInstanceOf[Future[U]]
      reactVar(tgt) {
        t =>
            if (future != null) {
              if (!future.isCompleted) {
                prevCallback.cancelled = true
              }
            }
            future = Future(f(t))
            prevCallback = new Cancellable[U](t => {
              GdxUtil.post {
                //err("post change:"+variable)
                variable = Some(t)
                //err("after change:"+variable)
                notifyObservers(variable)
              }
            })
            future.onComplete(prevCallback.apply)

      }

      def current: Option[Try[U]] = variable

      class Cancellable[U](f: Try[U] => Unit) extends ((Try[U]) => Unit) {
        var cancelled = false

        def apply(v1: Try[U]): Unit = if (!cancelled) f(v1)
      }

    }
  }

  implicit object VaryingFunctor extends Functor[Varying] {
    override def map[A, B](fa: Varying[A])(f: (A) => B): Varying[B] = fa.map(f)
  }

}