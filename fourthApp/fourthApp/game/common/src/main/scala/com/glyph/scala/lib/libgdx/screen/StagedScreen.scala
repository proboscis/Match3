package com.glyph.scala.lib.libgdx.screen

import com.glyph.scala.lib.util.screen.Screen
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL10

/**
 * @author glyph
 */
trait StagedScreen extends Screen{
  val STAGE_WIDTH :Int
  val STAGE_HEIGHT :Int
  val stage = new Stage(STAGE_WIDTH,STAGE_HEIGHT,true)
  Gdx.input.setInputProcessor(stage)

  override def render(delta: Float) {
    super.render(delta)
    Gdx.gl.glClearColor(1,1,1,1)
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT)
    stage.act(delta)
    stage.draw()
  }

  override def resize(w: Int, h: Int) {
    super.resize(w, h)
    stage.setViewport(STAGE_WIDTH, STAGE_HEIGHT, true)
    stage.getCamera.translate(-stage.getGutterWidth, -stage.getGutterHeight, 0)
  }
}
