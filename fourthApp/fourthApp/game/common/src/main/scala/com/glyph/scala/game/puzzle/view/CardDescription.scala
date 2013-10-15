package com.glyph.scala.game.puzzle.view

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.glyph.scala.lib.libgdx.actor.{TouchSource, OldDrawSprite, FuncTouchable}
import com.badlogic.gdx.graphics.g2d.{SpriteBatch, Sprite}
import com.glyph.java.asset.AM
import com.badlogic.gdx.graphics.{Color, Texture}
import com.badlogic.gdx.math.MathUtils
import com.glyph.scala.game.puzzle.model.cards.Card
import match3.PanelToken
import com.glyph.scala.ScalaGame
import com.glyph.scala.lib.util.reactive.Reactor
import com.glyph.scala.lib.libgdx.TextureUtil

/**
 * カードの説明を表示しまっせ
 * @author glyph
 */
abstract class BaseCardDescription(card:Card) extends Table with OldDrawSprite  with Reactor{
  val sprite: Sprite = new Sprite(TextureUtil.dummy)
  debug()
  setColor(Color.WHITE)
  val text = card.getClass.getSimpleName
  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    super.draw(batch, parentAlpha)
    commonFont.setColor(Color.BLACK)
    commonFont.draw(batch,text,getX,getY+getHeight/2)
  }
}
case class CardDescription(card: Card) extends BaseCardDescription(card)
case class PlayableCardDescription(card:Card#PlayableCard) extends BaseCardDescription(card.source)
