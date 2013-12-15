package com.glyph.scala.lib.util.updatable.task
import scala.collection.mutable
/**
 * @author glyph
 */
trait SequentialProcessor extends TaskProcessor {
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
      current.update(delta)
      if (current.isCompleted) {
        current.onFinish()
        current = null
      }
    }
  }

  def add(task: Task) :TaskProcessor={
    tasks += task
    this
  }

  def cancel(task: Task) {
    tasks.dequeueAll(_ == task)
    task.onCancel()
  }
}
