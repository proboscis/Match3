package com.glyph.scala.game.puzzle.view

import com.badlogic.gdx.scenes.scene2d.ui.{Image, Table}
import com.glyph.scala.lib.libgdx.actor.OldDrawSprite
import com.badlogic.gdx.graphics.g2d.{SpriteBatch, Sprite}
import panel.PanelToken
import com.badlogic.gdx.graphics.Color
import com.glyph.scala.ScalaGame
import com.glyph.scala.game.puzzle.model.puzzle.Panel

/**
 * @author glyph
 */
class PanelDescription(panel:Panel) extends Table with OldDrawSprite{
  val sprite: Sprite = new Sprite(PanelToken.texture)
  setColor(Color.WHITE)
  val text = panel.getClass.getSimpleName
  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    super.draw(batch, parentAlpha)
    commonFont.setColor(Color.BLACK)
    commonFont.draw(batch,""+panel.getClass.getSimpleName,getX,getY+getHeight/2)
  }
}
