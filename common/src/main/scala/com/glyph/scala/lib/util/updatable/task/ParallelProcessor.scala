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
    assert(updating == false)
    updating = true
    super.update(delta)
    //log("update"+this)
    queuedTasks foreach {
      t => t.onStart()
        startedTasks += t
    }
    queuedTasks.clear()

    for (t <- startedTasks) {
      if (!t.isCompleted) {
        if (!canceledTasks.isEmpty) {
          if (!canceledTasks.contains(t)) {
            t.update(delta)
          } else {
            tasksTobeRemoved += t
          }
        } else {
          t.update(delta)
        }
      } else {
        t.onFinish()
        tasksTobeRemoved += t
      }
    }
    val before = startedTasks.size
    tasksTobeRemoved foreach {
      t => {
        startedTasks -= t
        queuedTasks -= t
      }
    }
    val after = startedTasks.size
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
    if (updating == 0) {
      log("removing while updating")
      canceledTasks += task
    } else {
      startedTasks -= task
      queuedTasks -= task
    }
    task.onCancel()
  }
}

