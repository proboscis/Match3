package com.glyph.scala.lib.util.updatable.task

import com.glyph.scala.game.Glyphs
import com.glyph.scala.lib.util.pool.Pool

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
  import Glyphs._
  def apply(tasks: Task*): Sequence = {
    val seq = auto[Sequence]
    tasks foreach seq.add
    seq
  }
}