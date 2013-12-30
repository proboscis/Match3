package com.glyph.scala.lib.util.updatable.task


/**
 * @author glyph
 */
class Parallel extends ParallelProcessor with Task with AutoFree {
  def isCompleted: Boolean = startedTasks.size == 0 && queuedTasks.size == 0
  override def reset(){
    super.reset()
    clearTasks()
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
