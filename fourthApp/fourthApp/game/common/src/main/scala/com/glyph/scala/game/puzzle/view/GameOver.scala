package com.glyph.scala.game.puzzle.view

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.graphics.g2d.{Sprite, SpriteBatch, BitmapFontCache}
import com.glyph.scala.ScalaGame
import com.glyph.scala.lib.libgdx.TextureUtil
import com.glyph.scala.lib.libgdx.actor.DrawSprite
import com.badlogic.gdx.graphics.Color

/**
 * @author glyph
 */
class GameOver extends Actor with DrawSprite {
  val fontCache = new BitmapFontCache(ScalaGame.font)
  fontCache.setColor(Color.WHITE)
  fontCache.setText("GAME OVER", 0, 0)

  val back = new Sprite(TextureUtil.dummy)

  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    super.draw(batch, parentAlpha)
    drawSprite(batch, back, parentAlpha*0.8f, color = Color.BLACK)
    fontCache.setPosition(getWidth / 2 - fontCache.getBounds.width/2, getHeight / 2)
    fontCache.draw(batch)
  }
}
