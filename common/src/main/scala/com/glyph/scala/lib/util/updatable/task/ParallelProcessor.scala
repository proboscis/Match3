package com.glyph.scala.lib.util.updatable.task

import com.glyph.scala.lib.util.{Threading, Logging}
import com.badlogic.gdx.utils.{Array => GdxArray}

/**
 * @author glyph
 */
trait ParallelProcessor extends TaskProcessor with Logging with Threading {
  val queuedTasks = new GdxArray[Task]()
  val startedTasks = new GdxArray[Task]()
  val tasksTobeRemoved = new GdxArray[Task]()
  val canceledTasks = new GdxArray[Task]()
  var updating = false

  val queuedStarter = (t: Task) => {
    t.onStart()
    startedTasks add t
  }
  val tobeRemovedProcessor = (t: Task) => {
    startedTasks.removeValue(t, true)
    queuedTasks.removeValue(t, true)
  }

  override def update(delta: Float) {
    assert(!updating)
    updating = true
    super.update(delta)

    {
      val itr = queuedTasks.iterator()
      while (itr.hasNext) queuedStarter(itr.next())
      queuedTasks.clear()
    }

    {
      val startedItr = startedTasks.iterator()
      while (startedItr.hasNext) {
        val t = startedItr.next()
        if (canceledTasks.size > 0) {
          if (canceledTasks.contains(t, true)) {
            tasksTobeRemoved add t
          }
        } else if (!t.isCompleted) {
          t.update(delta)
          if (t.isCompleted) {
            t.onFinish()
            tasksTobeRemoved add t
          }
        } else {
          t.onFinish()
          tasksTobeRemoved add t
        }
      }
    }
    {
      val it = tasksTobeRemoved.iterator()
      while (it.hasNext) {
        tobeRemovedProcessor(it.next())
      }
      tasksTobeRemoved.clear()
    }
    canceledTasks.clear()
    updating = false
  }


  override def add(task: Task): TaskProcessor = {
    super.add(task)
    queuedTasks add task
    this
  }

  def contains(task: Task): Boolean = startedTasks.contains(task, true) || queuedTasks.contains(task, true)

  def cancel(task: Task) {
    if (!updating) {
      //log("removing while updating")
      if (!canceledTasks.contains(task, true)) {
        task.onCancel()
        canceledTasks add task
      }
    } else {
      if (queuedTasks.contains(task, true) || startedTasks.contains(task, true)) {
        task.onCancel()
      }
      startedTasks.removeValue(task, true)
      queuedTasks.removeValue(task, true)
    }
  }

  val canceller = (t: Task) => t.onCancel()

  def clearTasks() {
    queuedTasks.clear()

    {
      val it = startedTasks.iterator()
      while (it.hasNext) {
        canceller(it.next())
      }
      startedTasks.clear()
    }
    tasksTobeRemoved.clear()
    canceledTasks.clear()
  }
}

