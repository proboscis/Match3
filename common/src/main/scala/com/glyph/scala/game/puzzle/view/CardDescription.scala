package com.glyph.scala.game.puzzle.view

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.glyph.scala.lib.libgdx.actor.OldDrawSprite
import com.badlogic.gdx.graphics.g2d.{Batch, SpriteBatch, Sprite}
import com.badlogic.gdx.graphics.Color
import com.glyph.scala.game.puzzle.model.cards.Card
import com.glyph.scala.lib.util.reactive.Reactor
import com.glyph.scala.lib.libgdx.TextureUtil
import com.badlogic.gdx.assets.AssetManager

/**
 * カードの説明を表示しまっせ
 * @author glyph
 */
abstract class BaseCardDescription(assets:AssetManager,card: Card[_]) extends Table with OldDrawSprite with Reactor {
  val sprite: Sprite = new Sprite(TextureUtil.dummy(assets))
  debug()
  setColor(Color.WHITE)
  val text = card.getClass.getSimpleName
  val font = commonFont(assets)
  override def draw(batch: Batch, parentAlpha: Float) {
    super.draw(batch, parentAlpha)
    font.setColor(Color.BLACK)
    font.draw(batch, text, getX, getY + getHeight / 2)
  }
}

case class CardDescription(assets:AssetManager,card: Card[_]) extends BaseCardDescription(assets,card)
