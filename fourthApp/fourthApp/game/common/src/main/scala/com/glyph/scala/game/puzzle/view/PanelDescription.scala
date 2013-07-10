package com.glyph.scala.game.puzzle.view

import com.badlogic.gdx.scenes.scene2d.ui.{Image, Table}
import com.glyph.scala.game.puzzle.model.panels.Panel
import com.glyph.scala.lib.libgdx.actor.OldDrawSprite
import com.badlogic.gdx.graphics.g2d.{SpriteBatch, Sprite}
import panel.PanelToken
import com.badlogic.gdx.graphics.Color
import com.glyph.scala.ScalaGame

/**
 * @author glyph
 */
class PanelDescription(panel:Panel) extends Table with OldDrawSprite{
  val sprite: Sprite = new Sprite(PanelToken.texture)
  setColor(Color.WHITE)
  val text = panel.getClass.getSimpleName
  //TODO 説明文表示中のタップ判定を修正する。
  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    super.draw(batch, parentAlpha)
    ScalaGame.font.setColor(Color.BLACK)
    ScalaGame.font.draw(batch,""+panel.getClass.getSimpleName,getX,getY+getHeight/2)
  }
}
