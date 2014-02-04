package com.glyph.scala.lib.libgdx

import scala.concurrent.{ExecutionContext, Future}
import com.badlogic.gdx.Gdx

/**
 * @author glyph
 */
object GLFuture {
  object GLContext extends ExecutionContext {
    override def execute(runnable: Runnable): Unit = Gdx.app.postRunnable(runnable)
    override def reportFailure(t: Throwable): Unit = t.printStackTrace()
  }
  def apply[T](f: =>T):Future[T] =Future(f)(GLContext)
}
