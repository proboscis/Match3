package com.glyph.scala.lib.util.updatable.task

/**
 * @author glyph
 */
class Sequence extends SequentialProcessor with Task with AutoFree{
  def isCompleted: Boolean = current == null && tasks.isEmpty
  override def reset(): Unit = {
    super.reset()
    tasks.clear()
  }
}

object Sequence {
  def apply(tasks: Task*): Sequence = {
    val seq = new Sequence {}
    tasks foreach {
      seq.add
    }
    seq
  }
}