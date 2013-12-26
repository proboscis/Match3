package com.glyph.scala.lib.util.updatable.task

import com.glyph.scala.lib.util.updatable.Updatable
import com.glyph.scala.lib.util.Logging
/**
* @author glyph
*/
trait Task extends Updatable with Logging{
  var processor:TaskProcessor = null
  def isCompleted:Boolean
  def onStart(){}
  def onFinish(){}
  def onCancel(){}
  def reset(){}
  def cancel(){
    processor.cancel(this)
  }
}