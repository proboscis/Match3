package com.glyph.scala.game.puzzle.view

import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph.java.asset.AM
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.graphics.g2d.{SpriteBatch, Sprite}

/**
 * @author glyph
 */
class CardToken extends Actor {
  val sprite = new Sprite(AM.instance().get[Texture]("data/card" + MathUtils.random(1, 10) + ".png"))
  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    super.draw(batch, parentAlpha)
    sprite.setPosition(getX, getY)
    sprite.setRotation(getRotation)
    sprite.setScale(getScaleX, getScaleY)
    sprite.setSize(getWidth, getHeight)
    sprite.draw(batch, parentAlpha)
  }
}
