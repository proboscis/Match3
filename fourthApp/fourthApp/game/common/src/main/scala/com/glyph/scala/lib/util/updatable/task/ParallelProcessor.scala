package com.glyph.scala.lib.util.updatable.task

import com.glyph.scala.lib.util.collection.DoubleLinkedList

/**
 * @author glyph
 */
trait ParallelProcessor extends TaskProcessor{
  val tasks = new DoubleLinkedList[Task]
  override def update(delta: Float) {
    super.update(delta)
    tasks.foreach{
      task => if (!task.isCompleted) task.update(delta)
    }
  }

  def addTask(task: Task) {
    tasks.push(task)
  }

  def removeTask(task: Task) {
    tasks.remove(task)
  }
}
