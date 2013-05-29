package com.glyph.scala.lib.util.updatable.task

import com.glyph.scala.lib.util.callback.Callback

/**
 * @author glyph
 */
trait CompleteHook extends Task{
  protected var invoked = false
  val onComplete = new Callback
  override def update(delta: Float) {
    super.update(delta)
    if (isCompleted && !invoked){onComplete();invoked = true}
  }
}
