package com.glyph.scala.game.puzzle.view.panel

import com.badlogic.gdx.graphics._
import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph.scala.lib.libgdx.actor.{TouchSource, ObsTouchable, ExplosionFadeout, OldDrawSprite}
import com.badlogic.gdx.graphics.g2d.Sprite
import com.glyph.scala.game.puzzle.model.puzzle.Panel

/**
 * @author glyph
 */
abstract class PanelToken(val panel: Panel) extends Actor with OldDrawSprite with ExplosionFadeout with TouchSource{
  val sprite: Sprite = new Sprite(PanelToken.texture)
}

object PanelToken {
  val main = "2651BD"
  val image = new Pixmap(128, 128, Pixmap.Format.RGBA8888)
  image.setColor(Color.WHITE)
  image.fillRectangle(0, 0, image.getWidth, image.getHeight)
  val texture = new Texture(image)

  def apply(panel: Panel): PanelToken = {
    new ElementToken(panel)
  }
}
