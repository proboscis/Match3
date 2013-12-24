package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.screen.{ConfiguredScreen, ScreenBuilder}
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.Screen

/**
 * @author proboscis
 */
class ParticleTest extends ScreenBuilder{
  def requiredAssets: Set[(Class[_], Seq[String])] = Set()

  def create(assetManager: AssetManager): Screen = new ConfiguredScreen{

  }
}
