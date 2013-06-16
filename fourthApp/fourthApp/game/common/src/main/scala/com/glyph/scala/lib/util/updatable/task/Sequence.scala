package com.glyph.scala.lib.util.updatable.task

/**
 * @author glyph
 */
class Sequence(task:Task*) extends SequentialProcessor with Task{
  task foreach add
  def isCompleted: Boolean = current == null && tasks.isEmpty
}
object Sequence{
  def apply(tasks:Task*):Sequence={
    val seq = new Sequence {}
    tasks foreach {seq.add}
    seq
  }
}