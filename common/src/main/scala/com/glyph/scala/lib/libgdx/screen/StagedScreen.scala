package com.glyph.scala.lib.libgdx.screen

import com.glyph.scala.lib.util.screen.GlyphScreen
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.{Color, GL20, GL10}
import com.glyph.scala.lib.util.json.RVJSON
import scalaz._
import Scalaz._
import com.glyph.scala.lib.util.reactive.{Varying, Var}

/**
 * @author glyph
 */
trait StagedScreen extends GlyphScreen {
  def configSrc: RVJSON
  val config = configSrc
  /**
   * this color defines the clearing color
   */
  var backgroundColor = new Color(Color.BLACK)
  /**
   * set this flag to false if you want to manually control the timing of clearing screen
   */
  var autoClearScreen = true
  val stage = new Stage(STAGE_WIDTH, STAGE_HEIGHT, true)
  private val _STAGE_WIDTH:Int = config().flatMap(_.width.as[Int])|(1080 / 2)
  private val _STAGE_HEIGHT:Int = config().flatMap(_.height.as[Int])| (1920f * 15f / 16f / 2f).toInt
  def STAGE_WIDTH:Int = _STAGE_WIDTH
  def STAGE_HEIGHT:Int = _STAGE_HEIGHT
  override def show() {
    println("show StagedScreen")
    Gdx.input.setInputProcessor(stage)
    super.show()
  }

  override def render(delta: Float) {
    if(autoClearScreen) clearScreen()
    super.render(delta)
    stage.act(delta)
    stage.draw()
  }

  /**
   * use this method to clear the screen manually
   */
  def clearScreen(){
    Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a)
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT)
    Gdx.gl20.glTexParameterf(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_LINEAR)
  }

  override def resize(w: Int, h: Int) {
    super.resize(w, h)
    stage.setViewport(STAGE_WIDTH, STAGE_HEIGHT, true)
    stage.getCamera.translate(-stage.getGutterWidth, -stage.getGutterHeight, 0)
  }
}
