package com.glyph._scala.lib.libgdx.screen

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.graphics.g2d.{Sprite, Batch, BitmapFont, SpriteBatch}
import com.badlogic.gdx.graphics.{Texture, Color}
import com.glyph._scala.lib.util.json.RVJSON
import com.glyph._scala.lib.libgdx.reactive.GdxFile
import com.badlogic.gdx.assets.AssetManager
import scalaz._
import Scalaz._
import com.badlogic.gdx.Gdx
import com.glyph._scala.lib.libgdx.font.FontUtil
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.glyph._scala.lib.util.Logging

/**
 * @author glyph
 */
class LoadingScreen(onFinish: () => Unit, targetAM: AssetManager) extends StagedScreen with Logging{
  def configSrc: RVJSON = RVJSON(GdxFile("json/gameConfig.json"))
  override def show(): Unit = {
    super.show()
    backgroundColor = Color.DARK_GRAY
    val font = FontUtil.internalFont("font/corbert.ttf",STAGE_HEIGHT/20)//TODO dispose font
    println("Created LoadingScreen")
    val sprite = new Sprite(new Texture(Gdx.files.internal("data/dummy.png")))
    stage.addActor(new Actor {
      setPosition(STAGE_WIDTH/2,STAGE_HEIGHT/2)
      val twidth = font.getBounds("Loading...100%").width
      font.setColor(Color.WHITE)
      override def draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        font.draw(batch, "Loading...%.1f".format(targetAM.getProgress * 100), getX - twidth / 2, getY)
      }
    })
  }
  /*
  val font = strings.loaderFont.as[String].current.map {
    path => new BitmapFont(Gdx.files.internal(path), false)
  } | new BitmapFont()
  */


  override def render(delta: Float) {
    super.render(delta)
    println("loading...")
    if (targetAM.update()) {
      onFinish()
    }
  }
}
