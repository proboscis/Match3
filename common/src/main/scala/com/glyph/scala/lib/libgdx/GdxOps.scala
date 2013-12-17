package com.glyph.scala.lib.libgdx

import com.glyph.scala.lib.libgdx.poolable.PoolingGdxOps
import com.glyph.scala.lib.libgdx.conversion.{InterpolatableGdxOps, AnimatingGdxOps}
import com.glyph.scala.lib.libgdx.actor.SBDrawableGdxOps
import com.glyph.scala.lib.libgdx.actor.action.ActionOps

/**
 * @author glyph
 */
trait GdxOps
  extends PoolingGdxOps
  with AnimatingGdxOps
  with InterpolatableGdxOps
  with SBDrawableGdxOps
  with ActionOps
