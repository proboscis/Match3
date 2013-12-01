package com.glyph.scala.lib.util.updatable.task

import scalaz._
import Scalaz._
import com.glyph.scala.lib.util.Logging

/**
 * @author glyph
 */
class FunctionTask(var initializer:()=>Unit,var updater:Float=>Unit,var finalizer:()=>Unit)
  extends Task
  with AutoFree
  with Logging{
  private var finished = false
  def this() = this(null,null,null)
  def setFunctions(initializer:()=>Unit,updater:Float=>Unit,finalizer:()=>Unit):this.type ={
    this.initializer = initializer
    this.updater = updater
    this.finalizer = finalizer
    this
  }
  def setUpdater(updater:Float=>Unit){
    this.updater = updater
  }

  def finish(){
    finished = true
  }


  def isCompleted: Boolean = finished

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

  override def onFinish(){
    //log("onFinish")
    if(finalizer != null) finalizer()
    super.onFinish()
  }

  override def update(delta: Float){
    super.update(delta)
    updater(delta)
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