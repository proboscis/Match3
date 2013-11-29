package com.glyph.scala.lib.util.updatable.task

/**
 * @author glyph
 */
class OnFinish(var task:Task)(var callback:()=>Unit) extends Task with AutoFree{
  def this() = this(null)(null)
  def isCompleted: Boolean = task.isCompleted

  override def onStart(){
    super.onStart()
    task.onStart()
  }
  override def update(delta: Float): Unit ={
    super.update(delta)
    task.update(delta)
  }
  override def reset(){
    task = null
    callback = null
  }
  def setCallback(cb:()=>Unit){
    callback = cb
  }
  def setTask(tgt:Task){
    task = tgt
  }
  override def onFinish(): Unit = {
    callback()
    super.onFinish()
  }
}
