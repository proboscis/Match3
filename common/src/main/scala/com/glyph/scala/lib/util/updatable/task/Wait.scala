package com.glyph.scala.lib.util.updatable.task

import scala.collection.mutable.ListBuffer

/**
 * @author glyph
 */
class Wait(w:(Wait)=> Unit) extends Task{
  var invoked = false
  var woken = false
  def isCompleted: Boolean = invoked && woken
  override def update(delta: Float) {
    super.update(delta)
    if (!invoked){
      w(this)
      invoked = true
    }
  }
  def wake(){
    woken = true
  }
}
object Wait{
  def apply(w:(Wait)=>Unit):Wait={
    new Wait(w)
  }
}
class WaitAll(waiting:Task*) extends Task{
  val tasks = ListBuffer(waiting:_*)
  override def onStart(){
    super.onStart()
    waiting foreach{_.onStart()}
  }
  def isCompleted: Boolean = waiting forall(_.isCompleted)
  override def update(delta: Float){
    super.update(delta)
    for(task <- tasks){
      if(!task.isCompleted){
        task.update(delta)
      }else{
        tasks -= task
        task.onFinish()
      }
    }
  }
}

object WaitAll{
  def apply(tasks:Task*) = new WaitAll(tasks:_*)
}