package com.glyph.scala.lib.util.updatable.task

import com.glyph.scala.game.Glyphs


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
  import com.glyph.scala.lib.util.pool.GlobalPool.globals
  implicit val gen = Glyphs.genPooling[Parallel]
  def apply(tasks: Task*): Parallel = {
    val par = globals(classOf[Parallel]).auto
    tasks foreach {
      par.add
    }
    par
  }
}
