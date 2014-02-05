package com.glyph._scala.lib.libgdx

import scala.concurrent.{ExecutionContext, Future}
import com.badlogic.gdx.Gdx
import com.google.inject.Inject
import com.glyph._scala.lib.injection.GLExecutionContext

/**
 * @author glyph
 */
object GLFuture {
  @Inject val context:GLExecutionContext = null
  def apply[T](f: =>T):Future[T] =Future(f)(context)
}
