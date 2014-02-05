package com.glyph._scala.lib.injection

import scala.concurrent.ExecutionContext
import com.google.inject.Inject
import com.badlogic.gdx.Gdx
import com.google.inject.ImplementedBy
/**
 * @author glyph
 */
@ImplementedBy(classOf[DefaultGLExecutionContext])
trait GLExecutionContext  extends ExecutionContext
object GLExecutionContext extends ExecutionContext{
  @Inject
  val context :GLExecutionContext = null
  override def execute(runnable: Runnable): Unit = context.execute(runnable)
  override def reportFailure(t: Throwable): Unit = context.reportFailure(t)
}
class DefaultGLExecutionContext extends GLExecutionContext{
  override def execute(runnable: Runnable): Unit = Gdx.app.postRunnable(runnable)
  override def reportFailure(t: Throwable): Unit = t.printStackTrace()
}