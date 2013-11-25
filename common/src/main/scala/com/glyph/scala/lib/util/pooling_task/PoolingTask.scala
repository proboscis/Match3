package com.glyph.scala.lib.util.pooling_task

import com.glyph.scala.lib.util.updatable.task.{OnFinish, WaitAll, Sequence, Do}
import com.glyph.scala.lib.util.pool.Pooler

/**
 * @author glyph
 */
object PoolingTask {
  implicit object PoolerDo$ extends Pooler[Do]{
    def newInstance: Do = new Do(null)
    def reset(tgt: Do): Unit = tgt.reset()
  }
  implicit object PoolerSequence$ extends Pooler[Sequence]{
    def newInstance: Sequence = new Sequence
    def reset(tgt: Sequence): Unit = tgt.reset()
  }
  implicit object PoolerWaitAll$ extends Pooler[WaitAll]{
    def newInstance: WaitAll = new WaitAll
    def reset(tgt: WaitAll): Unit = tgt.reset()
  }
  implicit object PoolerOnFinish$ extends Pooler[OnFinish]{
    def newInstance: OnFinish = new OnFinish
    def reset(tgt: OnFinish): Unit = tgt.reset()
  }
}
