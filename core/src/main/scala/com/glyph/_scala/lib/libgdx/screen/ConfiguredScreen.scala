package com.glyph._scala.lib.libgdx.screen

import com.glyph._scala.lib.util.json.RVJSON
import com.glyph._scala.lib.libgdx.reactive.GdxFile
import scalaz._
import Scalaz._
import com.glyph._scala.lib.libgdx.game.LimitDelta

/**
 * @author glyph
 */
trait ConfiguredScreen extends TabledScreen with LimitDelta{
  def configSrc:RVJSON = RVJSON(GdxFile("json/gameConfig.json"))
  lazy val config: RVJSON = configSrc
  private lazy val stageWidth = config.width.as[Int].apply() | 1080
  private lazy val stageHeight= config.height.as[Int].apply() | (1920*15/16)
  private lazy val debugFlag = config.debug.as[Boolean].map(_| true)
  override def STAGE_WIDTH: Int = stageWidth

  override def STAGE_HEIGHT: Int = stageHeight

  override def drawDebugTable: Boolean = debugFlag()
}
