package com.glyph.scala.lib.libgdx.screen

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.graphics.g2d.{Batch, BitmapFont, SpriteBatch}
import com.badlogic.gdx.graphics.Color
import com.glyph.scala.lib.util.json.RVJSON
import com.glyph.scala.lib.libgdx.reactive.GdxFile
import com.badlogic.gdx.assets.AssetManager
import scalaz._
import Scalaz._
import com.badlogic.gdx.Gdx
import com.glyph.scala.lib.libgdx.font.FontUtil
import com.badlogic.gdx.scenes.scene2d.ui.Skin

/**
 * @author glyph
 */
class LoadingScreen(onFinish: () => Unit, targetAM: AssetManager) extends StagedScreen {
  def configSrc: RVJSON = RVJSON(GdxFile("json/gameConfig.json"))
  backgroundColor = Color.WHITE
  val strings = RVJSON(GdxFile("constants/string.js"))
  /*
  val font = strings.loaderFont.as[String].current.map {
    path => new BitmapFont(Gdx.files.internal(path), false)
  } | new BitmapFont()
  */
  val font = FontUtil.internalFont("font/corbert.ttf",STAGE_HEIGHT/20)
  println("Created LoadingScreen")
  stage.addActor(new Actor {
    font.setColor(Color.BLACK)
    setPosition(stage.getWidth / 2, stage.getHeight / 2)
    val twidth = font.getBounds("Loading...100%").width
    override def draw(batch: Batch, parentAlpha: Float) {
      super.draw(batch, parentAlpha)
      font.draw(batch, "Loading...%.1f%%".format(targetAM.getProgress * 100), getX - twidth / 2, getY)
    }
  })


  override def render(delta: Float) {
    super.render(delta)
    println("loading...")
    if (targetAM.update()) {
      onFinish()
    }
  }
}
