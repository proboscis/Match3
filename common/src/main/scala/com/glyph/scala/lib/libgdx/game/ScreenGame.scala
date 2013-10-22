package com.glyph.scala.lib.libgdx.game

import com.badlogic.gdx.{Game, Screen}
import com.glyph.scala.lib.libgdx.screen.LoadingScreen
import com.badlogic.gdx.assets.AssetManager

/**
 * @author glyph
 */
class ScreenGame(loader: AssetManager => Unit, screen: => Screen) extends ScreenBuilderSupport {
  override def create() {
    super.create()
    loader(assetManager)
    setScreen(new LoadingScreen(() => setScreen(screen),assetManager))
  }
}
