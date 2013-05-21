package com.glyph.scala.lib.util.updatable.task

import com.glyph.scala.lib.util.collection.DoubleLinkedQueue


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
      }
    }
    if (current != null) {
      current.update(delta)
      if (current.isCompleted) {
        current = null
      }
    }
  }

  def addTask(task: Task) {
    tasks.push(task)
  }

  def removeTask(task: Task) {
    tasks.remove(task)
  }
}
