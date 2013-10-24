package com.glyph.scala.game.puzzle.screen

import com.glyph.scala.lib.libgdx.screen.ScreenBuilder
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture

/**
 * @author glyph
 */
class PuzzleScreenBuilder extends ScreenBuilder {
  //I guess this can be written in the json too...
  def requiredAssets = Map(
    classOf[Skin] -> Array("skin/default.json"),
    classOf[Texture] -> Array(
      "data/particle.png",
      "data/sword.png",
      "data/dummy.png")
  )
  def create(assetManager: AssetManager): Screen = new PuzzleScreen(assetManager)
}
