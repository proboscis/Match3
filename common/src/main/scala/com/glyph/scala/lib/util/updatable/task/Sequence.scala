package com.glyph.scala.lib.util.updatable.task

/**
 * @author glyph
 */
class Sequence extends SequentialProcessor with Task with AutoFree{
  def isCompleted: Boolean = current == null && tasks.size == position

  override def reset(): Unit = {
    super[SequentialProcessor].reset()
    super[Task].reset()
    super[AutoFree].reset()
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