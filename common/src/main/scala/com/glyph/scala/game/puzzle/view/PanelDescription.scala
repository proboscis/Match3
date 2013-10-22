package com.glyph.scala.game.puzzle.view

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.glyph.scala.lib.libgdx.actor.{TouchSource, OldDrawSprite}
import com.badlogic.gdx.graphics.g2d.{SpriteBatch, Sprite}
import com.badlogic.gdx.graphics.Color
import com.glyph.scala.lib.libgdx.TextureUtil
import com.glyph.scala.lib.puzzle.Match3
import Match3.Panel
import com.glyph.scala.lib.puzzle.Match3
import com.badlogic.gdx.assets.AssetManager

/**
 * @author glyph
 */
class PanelDescription(assets:AssetManager,panel:Panel) extends Table with OldDrawSprite with TouchSource{
  val sprite: Sprite = new Sprite(TextureUtil.dummy(assets))
  setColor(Color.WHITE)
  val text = panel.toString
  val font = commonFont(assets)
  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    super.draw(batch, parentAlpha)
    font.setColor(Color.BLACK)
    font.draw(batch,""+text,getX,getY+getHeight/2)
  }
}
