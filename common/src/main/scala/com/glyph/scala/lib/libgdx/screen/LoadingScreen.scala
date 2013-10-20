package com.glyph.scala.lib.libgdx.screen

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.Color
import com.glyph.java.asset.AM
import com.glyph.scala.game.puzzle.view
import com.glyph.scala.lib.util.json.RJSON
import com.glyph.scala.lib.libgdx.reactive.GdxFile

/**
 * @author glyph
 */
class LoadingScreen(onFinish:() => Unit) extends StagedScreen {
  def configSrc: RJSON = RJSON(GdxFile("json/gameConfig.json").getString)
  println("Created LoadingScreen")
  var font = view.commonFont
  //view.commonFont

  stage.addActor(new Actor {
    font.setColor(Color.BLACK)
    setPosition(stage.getWidth / 2, stage.getHeight / 2)
    val twidth = font.getBounds("Loading...100%").width

    override def draw(batch: SpriteBatch, parentAlpha: Float) {
      super.draw(batch, parentAlpha)
      font.draw(batch, "Loading...%.1f%%".format(AM.instance().getProgress * 100), getX - twidth / 2, getY)
    }
  })


  override def render(delta: Float) {
    super.render(delta)
    println("loading...")
    if (AM.instance.update) {
      onFinish()
    }
  }
}
