package com.glyph.scala.game.puzzle.view

import com.glyph.scala.game.puzzle.model.Card
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.glyph.scala.lib.libgdx.actor.{OldDrawSprite, FuncTouchable}
import com.badlogic.gdx.graphics.g2d.Sprite
import com.glyph.java.asset.AM
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils

/**
 * カードの説明を表示しまっせ
 * @author glyph
 */
class CardDescription(card: Card) extends Table with FuncTouchable with OldDrawSprite {
  debug()
  val sprite = new Sprite(AM.instance().get[Texture]("data/card" + MathUtils.random(1, 10) + ".png"))
}
