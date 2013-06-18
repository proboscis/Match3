package com.glyph.scala.game.puzzle.view

import com.badlogic.gdx.graphics.{Texture, Color, Pixmap}
import com.badlogic.gdx.scenes.scene2d.{Action, Actor}
import com.glyph.scala.lib.libgdx.actor.{ExplosionFadeout, ActionUtil, Touchable, DrawSprite}
import com.badlogic.gdx.graphics.g2d.{SpriteBatch, BitmapFont, Sprite}
import com.glyph.scala.game.puzzle.model.panels.Panel
import com.glyph.scala.game.puzzle.model.panels.Panel.{Fire, Water, Thunder}

/**
 * @author glyph
 */
class PanelToken(val panel:Panel) extends Actor with DrawSprite with ExplosionFadeout{
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
    font.draw(batch,"D",getX+getWidth/2,getY+getHeight/2)
  }
}
object PanelToken{
  val main = "2651BD"
  val image = new Pixmap(128,128,Pixmap.Format.RGBA8888)
  val font = new BitmapFont()
  font.setColor(Color.WHITE)
  image.setColor("FFFFFF")
  image.fillRectangle(0,0,image.getWidth,image.getHeight)
  val texture = new Texture(image)
  implicit def strToColor(str:String):Color={
    Color.valueOf(str)
  }
}
