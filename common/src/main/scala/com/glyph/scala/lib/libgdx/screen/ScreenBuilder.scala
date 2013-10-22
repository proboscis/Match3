package com.glyph.scala.lib.libgdx.screen

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.Screen

/**
 * @author glyph
 */
trait ScreenBuilder {
  type Asset = (String,Class[_])
  def requiredAssets:List[Asset]
  def create(assetManager:AssetManager):Screen
}
