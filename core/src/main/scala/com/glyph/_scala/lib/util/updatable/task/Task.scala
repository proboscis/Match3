package com.glyph._scala.lib.util.updatable.task

import com.glyph._scala.lib.util.updatable.Updatable
import com.glyph._scala.lib.util.Logging
import com.glyph._scala.lib.util.pool.Pool

/**
* @author glyph
*/
trait Task extends Updatable with Logging{
  var processor:TaskProcessor = null
  def isCompleted:Boolean
  def onStart(){}
  def onFinish(){}
  def onCancel(){}
  def reset(){
    processor = null
  }
  def cancel(){
    if(processor != null)processor.cancel(this)
  }
}
