package com.glyph._scala.lib.util.updatable.task

import com.glyph._scala.lib.util.pool.Poolable

/**
 * automatically frees this task after finished.
 * @author glyph
 */
trait AutoFree extends Task with Poolable{
  override def onFinish(){
    super.onFinish()
    freeToPool()
  }

  override def onCancel(){
    super.onCancel()
    freeToPool()
  }
}
