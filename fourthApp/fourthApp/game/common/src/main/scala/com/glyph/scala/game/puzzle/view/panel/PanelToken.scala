package com.glyph.scala.game.puzzle.view.panel

import com.badlogic.gdx.graphics._
import com.badlogic.gdx.scenes.scene2d.{Touchable, Actor}
import com.glyph.scala.lib.libgdx.actor.{ObsTouchable, FuncTouchable, ExplosionFadeout, OldDrawSprite}
import com.badlogic.gdx.graphics.g2d.{BitmapFontCache, SpriteBatch, BitmapFont, Sprite}
import com.glyph.scala.game.puzzle.model.panels.Panel
import com.glyph.scala.game.puzzle.model.panels.Panel._
import com.badlogic.gdx.Gdx
import com.glyph.scala.ScalaGame

/**
 * @author glyph
 */
abstract class PanelToken(val panel: Panel) extends Actor with OldDrawSprite with ExplosionFadeout with ObsTouchable{
  val sprite: Sprite = new Sprite(PanelToken.texture)
}
object PanelToken {
  val main = "2651BD"
  val image = new Pixmap(128, 128, Pixmap.Format.RGBA8888)
  image.setColor(Color.WHITE)
  image.fillRectangle(0, 0, image.getWidth, image.getHeight)
  val texture = new Texture(image)
  def apply(panel:Panel):PanelToken={
    panel match {
      case _:Element=>new ElementToken(panel)
      case _:Monster=>new MonsterToken(panel)
      case _=>throw new RuntimeException("panel is not matching to any known instance")
    }
  }
}
