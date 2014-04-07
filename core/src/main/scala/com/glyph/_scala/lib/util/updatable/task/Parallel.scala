package com.glyph._scala.lib.util.updatable.task

import com.glyph._scala.game.Glyphs


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
  import com.glyph._scala.lib.util.pool.GlobalPool.globals
  import Glyphs._
  def apply(tasks: Task*): Parallel = {
    val par = auto[Parallel]
    tasks foreach {
      par.addTask
    }
    par
  }
}
