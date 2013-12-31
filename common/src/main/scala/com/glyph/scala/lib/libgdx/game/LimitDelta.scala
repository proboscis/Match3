package com.glyph.scala.lib.libgdx.game

import com.badlogic.gdx.{Screen, Game}
import com.glyph.scala.lib.util.screen.GlyphScreen
import com.badlogic.gdx.math.MathUtils

/**
 * @author glyph
 */
trait LimitDelta extends GlyphScreen{
  override def render(delta: Float): Unit = {
    super.render(Math.min(delta,0.032f))
  }
}
