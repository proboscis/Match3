package com.glyph._scala.lib.injection

import scala.concurrent.ExecutionContext
import com.badlogic.gdx.Gdx
import com.glyph._scala.lib.libgdx.gl.ShaderHandler

trait GLExecutionContext extends ExecutionContext

object GLExecutionContext extends ExecutionContext {
  var context: GLExecutionContext = new DefaultGLExecutionContext

  override def execute(runnable: Runnable): Unit = context.execute(runnable)

  override def reportFailure(t: Throwable): Unit = context.reportFailure(t)
}

class DefaultGLExecutionContext extends GLExecutionContext {
  override def execute(runnable: Runnable): Unit = Gdx.app.postRunnable(runnable)

  override def reportFailure(t: Throwable): Unit = t.printStackTrace()
}