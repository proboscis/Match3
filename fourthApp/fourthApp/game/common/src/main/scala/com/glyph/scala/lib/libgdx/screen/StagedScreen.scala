package com.glyph.scala.lib.libgdx.screen

import com.glyph.scala.lib.util.screen.Screen
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.{Color, GL20, GL10}

/**
 * @author glyph
 */
trait StagedScreen extends Screen{
  val backgroundColor = Color.WHITE
  def STAGE_WIDTH :Int
  def STAGE_HEIGHT :Int
  val stage = new Stage(STAGE_WIDTH,STAGE_HEIGHT,true)
  Gdx.input.setInputProcessor(stage)

  override def render(delta: Float) {
    super.render(delta)
    Gdx.gl.glClearColor(backgroundColor.r,backgroundColor.g,backgroundColor.b,backgroundColor.a)
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT)
    Gdx.gl20.glTexParameterf(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_LINEAR)
    stage.act(delta)
    stage.draw()
  }

  override def resize(w: Int, h: Int) {
    super.resize(w, h)
    stage.setViewport(STAGE_WIDTH, STAGE_HEIGHT, true)
    stage.getCamera.translate(-stage.getGutterWidth, -stage.getGutterHeight, 0)
  }
}
