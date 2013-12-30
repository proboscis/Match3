package com.glyph.scala.lib.util.updatable.task

import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import com.glyph.scala.lib.util.{Threading, Logging}

/**
 * @author glyph
 */
trait ParallelProcessor extends TaskProcessor with Logging with Threading {
  val queuedTasks = ListBuffer[Task]()
  val startedTasks = ArrayBuffer[Task]()
  val tasksTobeRemoved = ListBuffer[Task]()
  val canceledTasks = ArrayBuffer[Task]()
  var updating = false

  val queuedStarter = (t:Task)=>{
    t.onStart()
    startedTasks += t
  }
  val tobeRemovedProcessor = (t:Task)=>{
    startedTasks -= t
    queuedTasks -= t
  }
  override def update(delta: Float) {
    assert(!updating)
    updating = true
    super.update(delta)
    queuedTasks foreach queuedStarter
    queuedTasks.clear()

    {
      var i = 0
      val st = startedTasks
      val l = st.size
      while(i < l){
        val t = st(i)
        if(!canceledTasks.isEmpty){
          if(canceledTasks.contains(t)){
            tasksTobeRemoved += t
          }
        } else if (!t.isCompleted) {
          t.update(delta)
          if(t.isCompleted){
            t.onFinish()
            tasksTobeRemoved += t
          }
        } else {
          t.onFinish()
          tasksTobeRemoved += t
        }
        i += 1
      }
    }
    tasksTobeRemoved foreach tobeRemovedProcessor
    tasksTobeRemoved.clear()
    canceledTasks.clear()
    updating = false
  }


  override def add(task: Task): TaskProcessor = {
    super.add(task)
    queuedTasks += task
    this
  }

  def contains(task: Task): Boolean = startedTasks.contains(task) || queuedTasks.contains(task)

  def cancel(task: Task) {
    if (!updating) {
      //log("removing while updating")
      if(!canceledTasks.contains(task)){
        task.onCancel()
        canceledTasks += task
      }
    } else {
      if(queuedTasks.contains(task) || startedTasks.contains(task)){
        task.onCancel()
      }
      startedTasks -= task
      queuedTasks -= task
    }
  }
  val canceller = (t:Task) => t.onCancel()
  def clearTasks(){
    queuedTasks.clear()
    startedTasks foreach canceller
    startedTasks.clear()
    tasksTobeRemoved.clear()
    canceledTasks.clear()
  }
}

