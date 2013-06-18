package com.glyph.scala.game.puzzle.view

import com.badlogic.gdx.graphics._
import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph.scala.lib.libgdx.actor.{ExplosionFadeout, DrawSprite}
import com.badlogic.gdx.graphics.g2d.{BitmapFontCache, SpriteBatch, BitmapFont, Sprite}
import com.glyph.scala.game.puzzle.model.panels.Panel
import com.glyph.scala.game.puzzle.model.panels.Panel.{Fire, Water, Thunder}
import com.badlogic.gdx.Gdx
import com.glyph.scala.ScalaGame
import com.glyph.scala.game.puzzle.model.panels.Panel.Thunder
import com.glyph.scala.game.puzzle.model.panels.Panel.Fire
import com.glyph.scala.game.puzzle.model.panels.Panel.Water

/**
 * @author glyph
 */
class PanelToken(val panel: Panel) extends Actor with DrawSprite with ExplosionFadeout {

  import PanelToken._

  val sprite: Sprite = new Sprite(PanelToken.texture)
  this.setColor(panel match {
    case Thunder() => "ffcc00"
    case Water() => "88e02e"
    case Fire() => "ff8800"
    case _ => "000000"
  })

  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    super.draw(batch, parentAlpha)
    val b = cache.getBounds
    cache.setPosition(getX+(getWidth- b.width)/2,getY-(getHeight-b.height)/2 + getHeight)
    cache.setColor(Color.BLACK)
    cache.draw(batch,getColor.a*parentAlpha)
  }
}

object PanelToken {
  val main = "2651BD"
  val image = new Pixmap(128, 128, Pixmap.Format.RGBA8888)
  image.setColor("FFFFFF")
  image.fillRectangle(0, 0, image.getWidth, image.getHeight)
  val texture = new Texture(image)
  val cache = new BitmapFontCache(ScalaGame.font)
  cache.setText("D",0,0)
  implicit def strToColor(str: String): Color = {
    Color.valueOf(str)
  }
}
