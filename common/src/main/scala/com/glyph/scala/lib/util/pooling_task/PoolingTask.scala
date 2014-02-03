package com.glyph.scala.lib.util.pooling_task

import com.glyph.scala.game.Glyphs._
import com.glyph.scala.lib.util.updatable.task._
import com.glyph.scala.game.action_puzzle.MyTrail

/**
 * @author glyph
 */
trait PoolingTask {
  /*
  implicit val poolingTft = genPooling(classOf[TimedFunctionTask])
  implicit val poolingIft = genPooling(classOf[InterpolatedFunctionTask])
  implicit val poolingParallel = genPooling(classOf[Parallel])
  implicit val poolingSequence = genPooling(classOf[Sequence])
  implicit val poolingIntegratingTask = genPooling(classOf[IntegratingFTask])
  */
  implicit val poolingAutoFreeTask = genPooling[AutoFree]
}
