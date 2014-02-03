package com.glyph.scala.lib.util.updatable.task

import com.glyph.scala.game.Glyphs

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
  import com.glyph.scala.lib.util.pool.GlobalPool._
  implicit val gen = Glyphs.genPooling[Sequence]
  def apply(tasks: Task*): Sequence = {
    val seq = globals(classOf[Sequence]).auto
    tasks foreach seq.add
    seq
  }
}