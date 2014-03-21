package com.glyph._scala.test

import com.badlogic.gdx.graphics._
import com.glyph._scala.lib.libgdx.gl.{UVTrail, BaseStripBatch, ShaderHandler}
import com.badlogic.gdx.Screen
import com.glyph._scala.lib.libgdx.screen.ScreenBuilder
import com.badlogic.gdx.assets.AssetManager
import com.glyph._scala.game.Glyphs
import Glyphs._

/**
 * @author glyph
 */
class UVTrailTest extends ScreenBuilder {
  import com.glyph._scala.lib.libgdx.BuilderOps._
  def requirements = assetIsDescriptors(Seq(classOf[Texture] -> ("data/particle.png" :: Nil)))

  def create(implicit assetManager: AssetManager): Screen = {
    val texture: Texture = "data/particle.png".fromAssets
    new AppliedTrailTest(
      1000,
      new BaseStripBatch(1000 * 10 * 2, UVTrail.ATTRIBUTES),
      ShaderHandler("shader/rotate2.vert", "shader/default.frag"),
      () => new UVTrail(10),
      s => texture.bind(),
      true
    )
  }
}

