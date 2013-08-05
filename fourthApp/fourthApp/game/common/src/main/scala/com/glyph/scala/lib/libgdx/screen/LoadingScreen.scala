package com.glyph.scala.lib.libgdx.screen

import com.badlogic.gdx.scenes.scene2d.{Actor, Stage}
import com.badlogic.gdx.graphics.g2d.{BitmapFont, SpriteBatch}
import com.badlogic.gdx.graphics.{GL10, Color}
import com.badlogic.gdx.Gdx
import com.glyph.java.asset.AM
import com.glyph.scala.game.puzzle.view

/**
 * @author glyph
 */
class LoadingScreen extends Loader {
  println("LoadingScreen")
  val stage = new Stage()

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
    Gdx.gl.glClearColor(1, 1, 1, 1)
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT)
    stage.act(delta)
    stage.draw()
  }
}
