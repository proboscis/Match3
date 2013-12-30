package com.glyph.scala.lib.util

import com.glyph.scala.lib.util.animator.AnimatorOps
import com.glyph.scala.lib.util.pooling_task.{PoolingTask, PoolingOps}
import com.glyph.scala.lib.scalaz.ScalazOps
import com.glyph.scala.lib.util.reactive.{ReactiveOps, VaryingOps}
import com.glyph.scala.lib.util.pool.PoolOps

/**
 * @author glyph
 */
trait GlyphLibOps
  extends AnimatorOps
  with ScalazOps
  with VaryingOps
  with ReactiveOps
  with PoolOps
  with PoolingOps
  with PoolingTask
