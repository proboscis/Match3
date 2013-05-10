package com.glyph.scala

import com.badlogic.gdx.Game
import game.screen.{DecalTableTest, ShapeTestScene, Loading, GameScreen}
import com.badlogic.gdx.graphics.Texture
import com.glyph.libgdx.asset.AM
import com.badlogic.gdx.audio.Sound
import lib.util.gl.ViewportStack

/**
 * @author glyph
 */
class ScalaGame extends Game{
  def create() {
    AM.create()
    var i = 0
    while ( i <= 10){
      AM.instance().load("data/card" + i + ".png", classOf[Texture])
      i+=1
    }
    AM.instance().load("sound/drawcard.mp3", classOf[Sound])
    AM.instance().load("sound/gore.wav", classOf[Sound])
    AM.instance().load("data/background.png", classOf[Texture])
    AM.instance().load("data/skeleton.png", classOf[Texture])
    AM.instance().load("data/table.png", classOf[Texture])
    AM.instance().load("data/tile.png", classOf[Texture])
    AM.instance().load("data/rightArrow.png",classOf[Texture])
    AM.instance().load("data/leftArrow.png", classOf[Texture])
    AM.instance().load("data/lightbulb32.png", classOf[Texture])
    //AM.instance().finishLoading()
    //setScreen(new Loading(this))
    //setScreen(new ShapeTestScene)
    setScreen(new DecalTableTest)
  }

}
object ScalaGame{
  final val VIRTUAL_WIDTH = 540
  final val VIRTUAL_HEIGHT = 960
}
