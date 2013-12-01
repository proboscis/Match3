package com.glyph.scala.lib.util.updatable.task

import scalaz._
import Scalaz._

/**
 * @author glyph
 */
class FunctionTask(var initializer:()=>Unit,var updater:Float=>Unit,var finalizer:()=>Unit) extends TimedTask with AutoFree{
  def this() = this(null,null,null)
  def setFunctions(initializer:()=>Unit,updater:Float=>Unit,finalizer:()=>Unit){
    this.initializer = initializer
    this.updater = updater
    this.finalizer = finalizer
  }
  override def freeToPool(){
    super.freeToPool()
    initializer = null
    updater = null
    finalizer = null
  }

  override def onStart(){
    super.onStart()
    if(initializer != null)initializer()
  }

  override def onFinish(){
    if(finalizer != null) finalizer()
    super.onFinish()
  }

  override def update(delta: Float){
    super.update(delta)
    updater(delta)
  }
}
