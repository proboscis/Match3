package com.glyph.scala.lib.util.updatable.task

import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import com.glyph.scala.lib.util.{Threading, Logging}

/**
 * @author glyph
 */
trait ParallelProcessor extends TaskProcessor with Logging with Threading {
  val queuedTasks = ListBuffer[Task]()
  val startedTasks = ListBuffer[Task]()
  val tasksTobeRemoved = ListBuffer[Task]()
  val canceledTasks = ArrayBuffer[Task]()
  var updating = false

  override def update(delta: Float) {
    assert(!updating)
    updating = true
    super.update(delta)
    //log("update"+this)
    queuedTasks foreach {
      t => t.onStart()
        startedTasks += t
    }
    queuedTasks.clear()

    for (t <- startedTasks) {
      //log(t +","+t.isCompleted)
      if (!t.isCompleted) {
        if (!canceledTasks.isEmpty) {
          if (!canceledTasks.contains(t)) {
            t.update(delta)
            if(t.isCompleted){
              t.onFinish()
              tasksTobeRemoved += t
            }
          } else {
            tasksTobeRemoved += t
          }
        } else {
          t.update(delta)
          if(t.isCompleted){
            t.onFinish()
            tasksTobeRemoved += t
          }
        }
      } else {
        t.onFinish()
        tasksTobeRemoved += t
      }
    }
    tasksTobeRemoved foreach {
      t => {
        startedTasks -= t
        queuedTasks -= t
      }
    }
    tasksTobeRemoved.clear()
    canceledTasks.clear()
    updating = false
  }


  def add(task: Task): TaskProcessor = {
    queuedTasks += task
    this
  }

  def contains(task: Task): Boolean = startedTasks.contains(task) || queuedTasks.contains(task)

  def cancel(task: Task) {
    if (!updating) {
      //log("removing while updating")
      canceledTasks += task
    } else {
      startedTasks -= task
      queuedTasks -= task
    }
    task.onCancel()
  }
  def clearTasks(){
    queuedTasks.clear()
    startedTasks.clear()
    tasksTobeRemoved.clear()
    canceledTasks.clear()
  }
}

