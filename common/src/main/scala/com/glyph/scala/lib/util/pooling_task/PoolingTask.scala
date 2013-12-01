package com.glyph.scala.lib.util.pooling_task

import com.glyph.scala.lib.util.updatable.task._
import com.glyph.scala.lib.util.pool.Pooling

/**
 * these are so redundant!!!
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
  implicit object PoolingFunctionTask extends Pooling[FunctionTask]{
    def newInstance: FunctionTask = new FunctionTask
    def reset(tgt: FunctionTask): Unit = tgt.reset()
  }
  implicit object PoolingTimedFunctionTask extends Pooling[TimedFunctionTask]{
    def newInstance: TimedFunctionTask = new TimedFunctionTask

    def reset(tgt: TimedFunctionTask): Unit = tgt.reset()
  }
}
