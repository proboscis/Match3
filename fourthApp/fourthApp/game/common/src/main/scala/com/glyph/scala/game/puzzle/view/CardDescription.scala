package com.glyph.scala.game.puzzle.view

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.glyph.scala.lib.libgdx.actor.{OldDrawSprite, FuncTouchable}
import com.badlogic.gdx.graphics.g2d.{SpriteBatch, Sprite}
import com.glyph.java.asset.AM
import com.badlogic.gdx.graphics.{Color, Texture}
import com.badlogic.gdx.math.MathUtils
import com.glyph.scala.game.puzzle.model.cards.Card
import panel.PanelToken
import com.glyph.scala.ScalaGame

/**
 * カードの説明を表示しまっせ
 * @author glyph
 */
class CardDescription(card: Card) extends Table with FuncTouchable with OldDrawSprite {
  val sprite: Sprite = new Sprite(PanelToken.texture)
  debug()
  setColor(Color.WHITE)
  val text = card.getClass.getSimpleName
  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    super.draw(batch, parentAlpha)
    commonFont.setColor(Color.BLACK)
    commonFont.draw(batch,text,getX,getY+getHeight/2)
  }
}
