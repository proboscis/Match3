package com.glyph.scala.lib.util.pooling_task

import com.glyph.scala.lib.util.updatable.task.{OnFinish, WaitAll, Sequence, Do}
import com.glyph.scala.lib.util.pool.Pooling

/**
 * @author glyph
 */
object PoolingTask {
  implicit object PoolingDo extends Pooling[Do]{
    def newInstance: Do = new Do(null)
    def reset(tgt: Do): Unit = tgt.reset()
  }
  implicit object PoolingSequence extends Pooling[Sequence]{
    def newInstance: Sequence = new Sequence
    def reset(tgt: Sequence): Unit = tgt.reset()
  }
  implicit object PoolingWaitAll extends Pooling[WaitAll]{
    def newInstance: WaitAll = new WaitAll
    def reset(tgt: WaitAll): Unit = tgt.reset()
  }
  implicit object PoolingOnFinish extends Pooling[OnFinish]{
    def newInstance: OnFinish = new OnFinish
    def reset(tgt: OnFinish): Unit = tgt.reset()
  }
}
