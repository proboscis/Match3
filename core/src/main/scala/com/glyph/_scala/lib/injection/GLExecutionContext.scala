package com.glyph._scala.lib.injection

import scala.concurrent.ExecutionContext
import com.badlogic.gdx.Gdx
import com.glyph._scala.lib.util.Logging

trait GLExecutionContext extends ExecutionContext

object GLExecutionContext extends ExecutionContext with Logging{
  private var _context: GLExecutionContext = new DefaultGLExecutionContext
  def context = _context
  def context_=(c:GLExecutionContext) = _context.synchronized {
    log("replacing gl execution context:")
    log("previous",_context,"next",c)
    _context = c
  }
  override def execute(runnable: Runnable): Unit = _context.synchronized{
      _context.execute(runnable)
  }

  override def reportFailure(t: Throwable): Unit = _context.synchronized{
    _context.reportFailure(t)
  }
}

class DefaultGLExecutionContext extends GLExecutionContext with Logging{
  override def execute(runnable: Runnable): Unit = {
    log("executing task")
    Gdx.app.postRunnable(runnable)
  }

  override def reportFailure(t: Throwable): Unit = t.printStackTrace()
}