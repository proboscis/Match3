package com.glyph._scala.lib.libgdx

import scala.concurrent.Future
import com.glyph._scala.lib.injection.GLExecutionContext

/**
 * @author glyph
 */
object GLFuture {
  def apply[T](f: => T): Future[T] = Future(f)(GLExecutionContext)
}
