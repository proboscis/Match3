package com.glyph.scala.lib.util.updatable.task


/**
 * @author glyph
 */
trait Parallel extends ParallelProcessor with Task{
  def isCompleted: Boolean = tasks.forall(_.isCompleted)
}
