package com.glyph.scala.lib.util.updatable.task

import com.glyph.scala.lib.util.collection.list.DoubleLinkedList
import scala.collection.mutable.ListBuffer
import com.glyph.scala.lib.util.Logging

/**
 * @author glyph
 */
trait ParallelProcessor extends TaskProcessor with Logging{
  val queuedTasks = ListBuffer[Task]()
  val startedTasks = ListBuffer[Task]()
  var tasksTobeRemoved = ListBuffer[Task]()
  override def update(delta: Float) {
    super.update(delta)
    queuedTasks foreach {
      t => t.onStart()
        startedTasks += t
    }
    queuedTasks.clear()

    for(t <- startedTasks){
      if(!t.isCompleted){
        t.update(delta)
      }else{
        t.onFinish()
        tasksTobeRemoved += t
      }
    }
    val before = startedTasks.size
    tasksTobeRemoved foreach{
      t =>{
        startedTasks -= t
        queuedTasks -= t
      }
    }
    val after = startedTasks.size
    tasksTobeRemoved.clear()
  }


  def add(task: Task): TaskProcessor = {
    queuedTasks += task
    this
  }

  def contains(task:Task):Boolean = startedTasks.contains(task) || queuedTasks.contains(task)

  def removeTask(task: Task) {
    tasksTobeRemoved += task
  }
}
