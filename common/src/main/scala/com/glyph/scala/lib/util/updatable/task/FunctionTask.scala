package com.glyph.scala.lib.util.updatable.task

import scalaz._
import Scalaz._
import com.glyph.scala.lib.util.Logging

/**
 * @author glyph
 */
class BaseFTask(var initializer:()=>Unit,var updater:Float=>Unit,var finalizer:()=>Unit,var canceller:()=>Unit )
extends Task with AutoFree{
  def this() = this(null,null,null,null)
  var finished = false
  def isCompleted: Boolean = finished

  def finish(){
    finished = true
  }

  override def reset(){
    super.reset()
    initializer = null
    updater = null
    finalizer = null
    finished = false
  }
  override def onStart(){
    //log("onStart")
    super.onStart()
    if(initializer != null)initializer()
  }


  override def onCancel(){
    super.onCancel()
    canceller()
  }

  override def onFinish(){
    //log("onFinish")
    if(finalizer != null) finalizer()
    super.onFinish()
  }

  def setUpdater(f:Float=>Unit):this.type = {
    assert(f != null)
    this.updater = f
    this
  }
  def setFinalizer(f:()=>Unit):this.type = {
    this.finalizer = f
    this
  }
  def setCanceller(f:()=>Unit):this.type = {
    this.canceller = f
    this
  }
  def setFunctions(initializer:()=>Unit,updater:Float=>Unit,finalizer:()=>Unit):this.type = {
    this.initializer = initializer
    this.updater = updater
    this.finalizer = finalizer
    this
  }
}

class FunctionTask extends BaseFTask{
  override def update(delta: Float){
    super.update(delta)
    updater(delta)
  }
}

class IntegratingFTask extends BaseFTask{
  private var elapsedTime = 0f

  override def update(delta: Float){
    super.update(delta)
    if(this.isCompleted)throw new RuntimeException("why is this getting updated?")
    elapsedTime += delta
    updater(elapsedTime)
  }

  override def reset(){
    elapsedTime = 0
    super.reset()
  }
}

class TimedFunctionTask extends FunctionTask with TimedTask{
  override def isCompleted: Boolean = super[FunctionTask].isCompleted || super[TimedTask].isCompleted
  override def update(delta: Float){
    super[FunctionTask].update(delta)
    super[TimedTask].update(delta)
  }
  override def onStart(){
    super[TimedTask].onStart()
    super[FunctionTask].onStart()
  }
  override def onFinish(){
    super[FunctionTask].onFinish()
    super[TimedTask].onFinish()
  }
  override def reset(){
    super[FunctionTask].reset()
    super[TimedTask].reset()
  }
}
