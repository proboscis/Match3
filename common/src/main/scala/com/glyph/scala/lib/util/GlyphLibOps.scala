package com.glyph.scala.lib.util

import com.glyph.scala.lib.util.animator.AnimatorOps
import com.glyph.scala.lib.util.pooling_task.PoolingTaskOps
import com.glyph.scala.lib.util.pool.PoolOps
import com.glyph.scala.lib.scalaz.ScalazOps
import com.glyph.scala.lib.util.reactive.{ReactiveOps, VaryingOps}

/**
 * @author glyph
 */
trait GlyphLibOps
  extends AnimatorOps
  with PoolingTaskOps
  with PoolOps
  with ScalazOps
  with VaryingOps
  with ReactiveOps
