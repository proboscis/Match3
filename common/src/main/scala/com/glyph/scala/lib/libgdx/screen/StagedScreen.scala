package com.glyph.scala.lib.libgdx.screen

import com.glyph.scala.lib.util.screen.Screen
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.{Color, GL20, GL10}
import com.glyph.scala.lib.util.json.RJSON

/**
 * @author glyph
 */
trait StagedScreen extends Screen{
  def configSrc:RJSON
  val config = configSrc
  val backgroundColor = Color.WHITE
  val stage = new Stage(STAGE_WIDTH,STAGE_HEIGHT,true)
  def STAGE_WIDTH =config().width.as[Int].getOrElse(1080/2)


  def STAGE_HEIGHT = config().height.as[Int].getOrElse((1920f*15f/16f/2f).toInt)


  override def show() {
    println("show StagedScreen")
    Gdx.input.setInputProcessor(stage)
    super.show()
  }

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
