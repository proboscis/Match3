package com.glyph._scala.lib.libgdx.screen

import com.glyph._scala.lib.util.screen.GlyphScreen
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.{Color, GL20}
import com.glyph._scala.lib.util.json.RVJSON
import scalaz._
import Scalaz._
import com.glyph._scala.lib.util.reactive.{Varying, Var}
import com.glyph._scala.lib.util.Logging
import com.badlogic.gdx.utils.viewport.ExtendViewport

/**
 * @author glyph
 */
trait JSONStagedScreen extends StagedScreenBase {
  def configSrc: RVJSON
  val config = configSrc
  private val _STAGE_WIDTH:Int = config().flatMap(_.width.asOpt[Int])|(1080 / 2)
  private val _STAGE_HEIGHT:Int = config().flatMap(_.height.asOpt[Int])| (1920f * 15f / 16f / 2f).toInt
  def STAGE_WIDTH:Int = _STAGE_WIDTH
  def STAGE_HEIGHT:Int = _STAGE_HEIGHT
}

trait StagedScreenBase extends GlyphScreen with Logging{
  def STAGE_WIDTH:Int
  def STAGE_HEIGHT:Int
  /**
   * this color defines the clearing color
   */
  var backgroundColor = new Color(Color.BLACK)
  /**
   * set this flag to false if you want to manually control the timing of clearing screen
   */
  var autoClearScreen = true
  val viewport = new ExtendViewport(STAGE_WIDTH,STAGE_HEIGHT)
  val stage = new Stage(viewport)
  log("created stage:",STAGE_WIDTH,STAGE_HEIGHT)
  override def show() {
    log("show StagedScreen")
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
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT)
    Gdx.gl20.glTexParameterf(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_LINEAR)
  }

  override def resize(w: Int, h: Int) {
    super.resize(w, h)
    stage.getViewport.update(w,h,true)
    import viewport._
    log("viewport=>")
    "bottomGutterHeight"::getBottomGutterHeight::
      "topGutterHeight"::getTopGutterHeight::
      "topGutterY"::getTopGutterY::
      "leftGutterWidth"::getLeftGutterWidth::
      "rightGutterWidth"::getRightGutterWidth::
      "rightGutterX"::getRightGutterX::Nil foreach log
    log("viewport<=")
    log("w,h,sw,sh",w,h,STAGE_WIDTH,STAGE_HEIGHT)
    log("stageSize",stage.getWidth)
  }
}
