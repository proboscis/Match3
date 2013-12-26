package com.glyph.scala.lib.util.updatable.task

import scala.collection.mutable
import com.glyph.scala.lib.util.Logging

/**
 * @author glyph
 */
trait SequentialProcessor extends TaskProcessor with Logging{
  val tasks = new mutable.Queue[Task]()
  var current: Task = null

  override def update(delta: Float) {
    super.update(delta)

    if (current == null) {
      if (!tasks.isEmpty) {
        current = tasks.dequeue()
        current.onStart()
      }
    }
    if (current != null) {
      if (!current.isCompleted) {
        current.update(delta)
        if (current.isCompleted) {
          current.onFinish()
          current = null
        }
      }else{
        current.onFinish()
        current = null
      }
    }
  }

  def add(task: Task): TaskProcessor = {
    tasks += task
    this
  }

  def cancel(task: Task) {
    tasks.dequeueAll(_ == task)
    task.onCancel()
  }
}
