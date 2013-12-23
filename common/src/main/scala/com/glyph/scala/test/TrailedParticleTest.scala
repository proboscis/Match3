package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.gl.ShaderHandler
import com.glyph.scala.lib.libgdx.screen.ScreenBuilder
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.Screen

/**
 * @author glyph
 */
class TrailedParticleTest extends ScreenBuilder {
  def requiredAssets: Set[(Class[_], Seq[String])] = Set(classOf[Texture] -> Seq("data/particle.png"))
  def create(assetManager: AssetManager): Screen = new TrailTestEnvironment(
    ShaderHandler("shader/rotate2.vert", "shader/default.frag"),
    new BaseStripBatch(1000 * 20 * 2, UVTrail.ATTRIBUTES)) {
  }

}