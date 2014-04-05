package com.glyph._scala.lib.libgdx

import com.glyph._scala.lib.libgdx.poolable.PoolingGdxOps
import com.glyph._scala.lib.libgdx.conversion.{InterpolatorGdxOps, AnimatingGdxOps}
import com.glyph._scala.lib.libgdx.actor.{ActorOps, SBDrawableGdxOps}
import com.glyph._scala.lib.libgdx.actor.action.ActionOps
import com.glyph._scala.lib.libgdx.tween.GdxTweenOps
import com.glyph._scala.lib.libgdx.actor.table.AssetManagerOps
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedConstructorOps
import com.glyph._scala.lib.libgdx.gl.{TextureOps, GdxGlOps}
import com.glyph._scala.lib.libgdx.drawable.DrawableOps

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
  with BuilderOps
  with AnimatedConstructorOps
  with GdxGlOps
  with ActorOps
  with TextureOps
  with DrawableOps
  with FileOps
  with ImageOps
  with GdxUtilOps