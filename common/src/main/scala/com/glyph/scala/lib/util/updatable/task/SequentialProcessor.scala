package com.glyph.scala.lib.util.updatable.task
import com.glyph.scala.lib.util.collection.list.DoubleLinkedQueue

/**
 * @author glyph
 */
trait SequentialProcessor extends TaskProcessor {
  val tasks = new DoubleLinkedQueue[Task]()
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
    tasks.push(task)
    this
  }

  def removeTask(task: Task) {
    tasks.remove(task)
  }
}
