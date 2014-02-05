package com.glyph._scala.lib.libgdx

import com.glyph._scala.lib.libgdx.poolable.PoolingGdxOps
import com.glyph._scala.lib.libgdx.conversion.{InterpolatorGdxOps, AnimatingGdxOps}
import com.glyph._scala.lib.libgdx.actor.SBDrawableGdxOps
import com.glyph._scala.lib.libgdx.actor.action.ActionOps
import com.glyph._scala.lib.libgdx.tween.GdxTweenOps
import com.glyph._scala.lib.libgdx.actor.table.AssetManagerOps

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
  with AssetManagerOps
