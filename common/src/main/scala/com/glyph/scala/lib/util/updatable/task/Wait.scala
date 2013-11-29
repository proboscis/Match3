package com.glyph.scala.lib.util.updatable.task

import scala.collection.mutable.ListBuffer
import com.glyph.scala.lib.util.Logging

/**
 * @author glyph
 */
class Wait(w:(Wait)=> Unit) extends Task with Logging{
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
trait Waitable
class TWait
class WaitAll(waiting:Task*) extends Task with Logging with AutoFree{
  val tasks = ListBuffer(waiting:_*)
  override def onStart(){
    super.onStart()
    tasks foreach{_.onStart()}
  }
  def isCompleted: Boolean = tasks forall(_.isCompleted)
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

  def add(t:Task){
    tasks += t
  }

  override def reset(): Unit = {
    super.reset()
    tasks.clear()
  }
}

object WaitAll{
  //def apply(tasks:Seq[Task]) = new WaitAll(tasks:_*)
  def apply(tasks:Task*) = new WaitAll(tasks:_*)
}