package com.glyph._scala.test

import com.glyph._scala.game.action_puzzle.view.animated.LazyAssets
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager
import com.glyph._scala.lib.libgdx.gl.ShaderUtil
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager.AnimatedConstructor
import com.glyph._scala.game.Glyphs._
import com.glyph._scala.game.builders.Builders
import com.glyph._scala.lib.libgdx.Builder

/**
 * @author glyph
 */
class DistanceFieldTest extends MockTransition with LazyAssets {
  val shader = ShaderUtil.load("shader/dist.vert", "shader/dist.frag")
  val screen: AnimatedConstructor = shader.map(_.map(_.map {
    sp => Builder()
  }))

  override def graph: AnimatedManager.AnimatedGraph = super.graph
}