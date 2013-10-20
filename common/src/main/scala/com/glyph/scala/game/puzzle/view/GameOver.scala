package com.glyph.scala.game.puzzle.view

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.graphics.g2d.{Sprite, SpriteBatch, BitmapFontCache}
import com.glyph.scala.ScalaGame
import com.glyph.scala.lib.libgdx.TextureUtil
import com.glyph.scala.lib.libgdx.actor.DrawSprite
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions._
import com.badlogic.gdx.math.Interpolation

/**
 * @author glyph
 */
class GameOver extends Actor with DrawSprite {
  val fontCache = new BitmapFontCache(commonFont)
  fontCache.setColor(Color.WHITE)
  fontCache.setText("GAME OVER", 0, 0)

  val back = new Sprite(TextureUtil.dummy)
  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    val alpha = parentAlpha * 0.8f
    super.draw(batch, alpha)
    drawSprite(batch, back, alpha)
    fontCache.setPosition(getWidth / 2 - fontCache.getBounds.width/2, getHeight / 2)
    fontCache.draw(batch,alpha)
  }
  setColor(0, 0, 0, 0)
  addAction(fadeIn(0.7f, Interpolation.exp10Out))
}
