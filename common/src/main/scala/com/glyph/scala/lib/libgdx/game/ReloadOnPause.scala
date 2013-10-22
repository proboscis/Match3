package com.glyph.scala.lib.libgdx.game

import com.badlogic.gdx._
import com.glyph.scala.lib.libgdx.screen.{ScreenBuilder, LoadingScreen}
import com.glyph.scala.lib.util.{Logging, MemoryAnalyzer}
import com.badlogic.gdx.graphics.GL10
import scala.Some
import com.badlogic.gdx.assets.AssetManager
import scala.annotation.target

/**
 * @author glyph
 */
trait ReloadOnPause extends Game with Logging {
  val assetManager = new AssetManager

  var pausedScreen: Option[Screen] = None

  override def resume() {
    super.resume()
    println("resume!")
    if(!assetManager.update()){
    setScreen(new LoadingScreen(() => {
      pausedScreen foreach setScreen
    }, assetManager))
    }else{
      pausedScreen foreach setScreen
    }
    pausedScreen = None
  }


  override def setScreen(screen: Screen) {
    println("setScreen:" + screen)
    super.setScreen(screen)
  }

  override def pause() {
    super.pause()
    println("pause!")
    pausedScreen = Some(getScreen)
  }

  def create() {
    new MemoryAnalyzer
  }

  override def render() {
    Gdx.gl.glClearColor(0, 0, 0, 0)
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT)
    super.render()
  }
}
