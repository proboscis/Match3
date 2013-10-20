package com.glyph.scala.lib.util.updatable.task


/**
 * @author glyph
 */
class Parallel(task:Task*) extends ParallelProcessor with Task{
  task foreach add
  def isCompleted: Boolean = tasks.forall(_.isCompleted)
}
object Parallel{
  def apply(tasks:Task*):Parallel={
    val par = new Parallel{}
    tasks foreach {par.add}
    par
  }
}
