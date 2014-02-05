package com.glyph._scala.lib.util.extraction

import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
 * an extractor can extract T in E
 * target may be Future object or Builders etc.
 * @author glyph
 */
trait Extractable[E[_]]{
  def extract[T](target:E[T])(callback:T=>Unit)
  def isExtracted[T](target:E[T]):Boolean
}
object ExtractableFuture extends Extractable[Future]{
  import scala.concurrent.ExecutionContext.Implicits.global
  override def extract[T](target: Future[T])(callback: (T) => Unit): Unit = target.onComplete{
    case Success(s) => callback(s)
    case Failure(f) => f.printStackTrace()
  }
  override def isExtracted[T](target: Future[T]): Boolean = target.isCompleted
}

object ExtractableFunction0 extends Extractable[Function0]{
  import scala.concurrent.ExecutionContext.Implicits.global
  override def extract[T](target: () => T)(callback: (T) => Unit): Unit = Future(target()).onComplete{
    case Success(s) => callback(s)
    case Failure(f) => f.printStackTrace()
  }
  override def isExtracted[T](target: () => T): Boolean = false
}

/**
 * type lambda! for higher kinded types.
 */
object ExtractableFunctionFuture extends Extractable[({type l[A] = ()=>Future[A]})#l]{
  import scala.concurrent.ExecutionContext.Implicits.global
  override def extract[T](target: () => Future[T])(callback: (T) => Unit): Unit = target().onComplete{
    case Success(s) => callback(s)
    case Failure(f) => f.printStackTrace()
  }
  override def isExtracted[T](target: () => Future[T]): Boolean = false
}