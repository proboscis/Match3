package com.glyph.scala.lib.libgdx.game

import com.badlogic.gdx.{Game, Screen}
import com.glyph.scala.lib.libgdx.screen.LoadingScreen
import com.glyph.java.asset.AM
import com.badlogic.gdx.assets.AssetManager

/**
 * @author glyph
 */
class ScreenGame(loader:AssetManager=>Unit,screen: =>Screen) extends Game{
  def create() {
    AM.create()
    loader(AM.instance())
    setScreen( new LoadingScreen(()=>setScreen(screen)))
  }
}
