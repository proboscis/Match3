package com.glyph.scala.lib.util.updatable.task

/**
 * @author glyph
 */
trait Sequence extends SequentialProcessor with Task{
  def isCompleted: Boolean = current == null && tasks.isEmpty
}