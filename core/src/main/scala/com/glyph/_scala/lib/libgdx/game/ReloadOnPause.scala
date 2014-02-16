package com.glyph._scala.lib.libgdx.game

import com.badlogic.gdx._
import com.glyph._scala.lib.libgdx.screen.LoadingScreen
import com.glyph._scala.lib.util.Logging
import com.badlogic.gdx.graphics.GL10
import scala.Some
import com.badlogic.gdx.assets.AssetManager

/**
 * @author glyph
 */
trait ReloadOnPause extends Game with Logging {
  val assetManager = new AssetManager


  override def resume() {
    super.resume()
    println("resume!")
    val current = getScreen
    if (current != null) {
      if (!assetManager.update(5)) {
        setScreen(new LoadingScreen(() => {
          setScreen(current)
        }, assetManager))
      } else {
        setScreen(current)
      }
    }
  }


  override def setScreen(screen: Screen) {
    println("setScreen:" + screen)
    super.setScreen(screen)
  }

  override def pause() {
    super.pause()
    println("pause!")
  }

  def create() {
    // new MemoryAnalyzer
  }

  override def render() {
    Gdx.gl.glClearColor(0, 0, 0, 0)
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT)
    super.render()
  }

  override def dispose() {
    assetManager.dispose()
  }
}
