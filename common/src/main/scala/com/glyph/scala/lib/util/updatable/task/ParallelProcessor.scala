package com.glyph.scala.lib.util.updatable.task

import com.glyph.scala.lib.util.collection.list.DoubleLinkedList
import scala.collection.mutable.ListBuffer

/**
 * @author glyph
 */
trait ParallelProcessor extends TaskProcessor {
  val queuedTasks = ListBuffer[Task]()
  val startedTasks = ListBuffer[Task]()
  var tasksTobeRemoved = ListBuffer[Task]()
  override def update(delta: Float) {
    super.update(delta)
    queuedTasks foreach {
      t => t.onStart()
        startedTasks += t
    }
    for(t <- startedTasks){
      if(!t.isCompleted){
        t.update(delta)
      }else{
        t.onFinish()
        tasksTobeRemoved += t
      }
    }
    tasksTobeRemoved foreach{
      t =>
        startedTasks -= t
        queuedTasks -= t
    }
    tasksTobeRemoved.clear()
  }

  def add(task: Task): TaskProcessor = {
    queuedTasks += task
    this
  }

  def removeTask(task: Task) {
    tasksTobeRemoved += task
  }
}
