package com.glyph.scala

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.Texture
import com.glyph.java.asset.AM
import lib.libgdx.screen.LoadingScreen

/**
 * @author glyph
 */
class ScalaGame extends Game {
  def create() {
    AM.create()
    var i = 1
    while (i <= 10) {
      //AM.instance().load("data/card" + i + ".png", classOf[Texture])
      i += 1
    }

      /**
       * testing comment
       */
    /*
    AM.instance().load("sound/drawcard.mp3", classOf[Sound])
    AM.instance().load("sound/gore.wav", classOf[Sound])
    AM.instance().load("data/background.png", classOf[Texture])
    AM.instance().load("data/table.png", classOf[Texture])
    AM.instance().load("data/tile.png", classOf[Texture])
    AM.instance().load("data/rightArrow.png",classOf[Texture])
    AM.instance().load("data/leftArrow.png", classOf[Texture])
    */
    AM.instance().load("data/TileA4.png", classOf[Texture])
    AM.instance().load("data/skeleton.png", classOf[Texture])
    //AM.instance().load("data/lightbulb32.png", classOf[Texture])
    //AM.instance().finishLoading()
    val loading = new LoadingScreen(() => {
      //setScreen(new GameScreen(this))
      //setScreen(new DecalTableTest)
      //setScreen(new Loading(this))
    })
    setScreen(loading)
  }
}
