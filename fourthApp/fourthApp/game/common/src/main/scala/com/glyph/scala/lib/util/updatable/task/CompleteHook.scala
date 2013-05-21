package com.glyph.scala.lib.util.updatable.task

/**
 * @author glyph
 */
trait CompleteHook extends Task{
  protected var invoked = false
  override def update(delta: Float) {
    super.update(delta)
    if (isCompleted && !invoked){onComplete();invoked = true}
  }
  def onComplete()
}
