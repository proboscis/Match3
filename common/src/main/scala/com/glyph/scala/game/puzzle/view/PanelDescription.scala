package com.glyph.scala.game.puzzle.view

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.glyph.scala.lib.libgdx.actor.{TouchSource, OldDrawSprite}
import com.badlogic.gdx.graphics.g2d.{SpriteBatch, Sprite}
import com.badlogic.gdx.graphics.Color
import com.glyph.scala.lib.libgdx.TextureUtil
import com.glyph.scala.lib.puzzle.Match3
import Match3.Panel
import com.glyph.scala.lib.puzzle.Match3

/**
 * @author glyph
 */
class PanelDescription(panel:Panel) extends Table with OldDrawSprite with TouchSource{
  val sprite: Sprite = new Sprite(TextureUtil.dummy)
  setColor(Color.WHITE)
  val text = panel.toString
  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    super.draw(batch, parentAlpha)
    commonFont.setColor(Color.BLACK)
    commonFont.draw(batch,""+text,getX,getY+getHeight/2)
  }
}
