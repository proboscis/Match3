package com.glyph.scala.lib.util.updatable.task


/**
 * @author glyph
 */
class Parallel extends ParallelProcessor with Task with AutoFree {

  val addedTasks = scala.collection.mutable.ListBuffer[Task]()
  def isCompleted: Boolean = addedTasks.forall(_.isCompleted)
  override def reset(){
    super.reset()
    clearTasks()
    addedTasks.clear()
  }

  override def add(task: Task): TaskProcessor = {
    addedTasks += task
    super.add(task)
  }
}

object Parallel {
  def apply(tasks: Task*): Parallel = {
    val par = new Parallel {}
    tasks foreach {
      par.add
    }
    par
  }
}
