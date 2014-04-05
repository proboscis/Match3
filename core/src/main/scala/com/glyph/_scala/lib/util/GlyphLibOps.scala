package com.glyph._scala.lib.util

import com.glyph._scala.lib.util.animator.AnimatorOps
import com.glyph._scala.lib.util.pooling_task.{PoolingTask, PoolingOps}
import com.glyph._scala.lib.scalaz.ScalazOps
import com.glyph._scala.lib.util.reactive.{ReactiveOps, VaryingOps}
import com.glyph._scala.lib.util.pool.PoolOps
import com.glyph.ClassMacro

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
  with ClassMacro
  with ProfilingOps
