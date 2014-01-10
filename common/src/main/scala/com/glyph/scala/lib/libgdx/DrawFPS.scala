package com.glyph.scala.lib.libgdx

import com.badlogic.gdx.{Game, Gdx}
import com.badlogic.gdx.graphics.{GL10, OrthographicCamera}
import com.badlogic.gdx.graphics.g2d.{SpriteBatch, BitmapFont}
import com.glyph.scala.lib.util.FPS

/**
 * @author glyph
 */
trait DrawFPS extends Game {
  val fps = new FPS
  lazy val debugBatch = new SpriteBatch(100)

  def debugFont: BitmapFont

  lazy val camera = new OrthographicCamera(Gdx.graphics.getWidth, Gdx.graphics.getHeight)

  override def render(): Unit = {
    super.render()
    fps() = Gdx.graphics.getDeltaTime
    camera.update()
    debugBatch.setProjectionMatrix(camera.combined)
    Gdx.gl.glActiveTexture(GL10.GL_TEXTURE0)
    debugBatch.begin()
    debugFont.draw(debugBatch, "%.0f".format(fps.fps), -Gdx.graphics.getWidth / 2, +Gdx.graphics.getHeight / 2) //+ debugFont.getLineHeight)
    debugBatch.end()
  }
}
