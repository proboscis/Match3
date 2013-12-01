package com.glyph.scala.lib.util.updatable.task

/**
 * @author glyph
 */
class InterpolatedFunctionTask(var initializer:()=>Unit,var updater:Float=>Unit,var finalizer:()=>Unit) extends InterpolationTask{
  def this() = this(null,null,null)
  def setFunctions(initializer:()=>Unit,updater:Float=>Unit,finalizer:()=>Unit):this.type = {
    this.initializer = initializer
    this.updater = updater
    this.finalizer =finalizer
    this
  }

  override def reset(){
    super.reset()
    initializer = null
    updater = null
    finalizer = null
  }

  override def onStart(){
    super.onStart()
    if(initializer != null)initializer()
  }

  override def onFinish(){
    if(finalizer != null)finalizer()
    super.onFinish()
  }

  def apply(alpha: Float){
    updater(alpha)
  }
}
