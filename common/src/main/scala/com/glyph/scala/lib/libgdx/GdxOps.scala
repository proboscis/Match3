package com.glyph.scala.lib.libgdx

import com.glyph.scala.lib.libgdx.poolable.PoolingGdxOps
import com.glyph.scala.lib.libgdx.conversion.{InterpolatorGdxOps, AnimatingGdxOps}
import com.glyph.scala.lib.libgdx.actor.SBDrawableGdxOps
import com.glyph.scala.lib.libgdx.actor.action.ActionOps
import com.glyph.scala.lib.libgdx.tween.GdxTweenOps

/**
 * @author glyph
 */
trait GdxOps
  extends PoolingGdxOps
  with AnimatingGdxOps
  with InterpolatorGdxOps
  with SBDrawableGdxOps
  with ActionOps
  with GdxTweenOps
  with GdxStringOps
