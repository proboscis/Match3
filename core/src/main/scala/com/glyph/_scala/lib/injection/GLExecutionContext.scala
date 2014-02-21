package com.glyph._scala.lib.injection

import scala.concurrent.ExecutionContext
import com.badlogic.gdx.Gdx

trait GLExecutionContext extends ExecutionContext

object GLExecutionContext extends ExecutionContext {
  private var _context: GLExecutionContext = new DefaultGLExecutionContext
  def context_=(c:GLExecutionContext) = _context.synchronized {_context = c}
  def context = _context.synchronized{_context}
  override def execute(runnable: Runnable): Unit = _context.synchronized{
      _context.execute(runnable)
  }

  override def reportFailure(t: Throwable): Unit = _context.synchronized{
    _context.reportFailure(t)
  }
}

class DefaultGLExecutionContext extends GLExecutionContext {
  override def execute(runnable: Runnable): Unit = Gdx.app.postRunnable(runnable)

  override def reportFailure(t: Throwable): Unit = t.printStackTrace()
}