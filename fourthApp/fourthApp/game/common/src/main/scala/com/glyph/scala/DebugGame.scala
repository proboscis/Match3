package com.glyph.scala

import com.badlogic.gdx.{Gdx, Game}
import com.glyph.java.asset.AM
import com.badlogic.gdx.graphics.{GL10, Texture}
import game.screen.{ScrollTestScreen, DebugScreen, LoadingScreen}
import com.badlogic.gdx.audio.Sound

/**
 * @author glyph
 */
class DebugGame extends Game {
  def create() {
    AM.create()
    var i = 1
    while (i <= 10) {
      AM.instance().load("data/card" + i + ".png", classOf[Texture])
      i += 1
    }

    AM.instance().load("sound/drawcard.mp3", classOf[Sound])
    AM.instance().load("sound/gore.wav", classOf[Sound])
    AM.instance().load("data/background.png", classOf[Texture])
    AM.instance().load("data/table.png", classOf[Texture])
    AM.instance().load("data/tile.png", classOf[Texture])
    AM.instance().load("data/rightArrow.png",classOf[Texture])
    AM.instance().load("data/leftArrow.png", classOf[Texture])

    AM.instance().load("data/TileA4.png", classOf[Texture])
    AM.instance().load("data/skeleton.png", classOf[Texture])
    //AM.instance().load("data/lightbulb32.png", classOf[Texture])
    //AM.instance().finishLoading()
    val loading = new LoadingScreen
    loading.onFinish += (() => {
      setScreen(new DebugScreen)
      //setScreen(new ScrollTestScreen)
    })
    setScreen(loading)
  }

  override def render() {
    Gdx.gl.glClearColor(0, 0, 0, 0)
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT)
    super.render()
  }
}
