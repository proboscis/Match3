package com.glyph.scala.lib.libgdx.screen

import com.glyph.scala.lib.util.screen.Screen
import com.badlogic.gdx.scenes.scene2d.{Actor, Stage}
import com.badlogic.gdx.graphics.g2d.{BitmapFont, SpriteBatch}
import com.badlogic.gdx.graphics.{GL10, Color}
import com.badlogic.gdx.Gdx
import com.glyph.java.asset.AM

/**
 * @author glyph
 */
class LoadingScreen extends Screen with Loader {
  val stage = new Stage()
  stage.addActor(new Actor {
    val font = new BitmapFont()
    font.setColor(Color.WHITE)
    setPosition(stage.getWidth / 2, stage.getHeight / 2)
    override def draw(batch: SpriteBatch, parentAlpha: Float) {
      super.draw(batch, parentAlpha)
      font.draw(batch, "Loading...%.1f%%".format(AM.instance().getProgress * 100), getX, getY)
    }
  })

  override def render(delta: Float) {
    super.render(delta)
    Gdx.gl.glClearColor(0, 0, 0, 0)
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT)
    stage.act(delta)
    stage.draw()
  }
}
