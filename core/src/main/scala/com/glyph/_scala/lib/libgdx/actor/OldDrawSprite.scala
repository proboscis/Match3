package com.glyph._scala.lib.libgdx.actor

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.graphics.g2d.{Batch, Sprite, SpriteBatch}
import com.badlogic.gdx.math.Vector2

/**
 * @author glyph
 */
trait OldDrawSprite extends Actor {
  val sprite: Sprite
  val offset = new Vector2
  override def draw(batch: Batch, parentAlpha: Float) {
    super.draw(batch, parentAlpha)
    sprite.setSize(getWidth, getHeight)
    sprite.setOrigin(getOriginX,getOriginY)
    sprite.setPosition(getX+offset.x, getY+offset.y)
    sprite.setScale(getScaleX, getScaleY)
    sprite.setRotation(getRotation)
    sprite.setColor(getColor)
    sprite.draw(batch, parentAlpha)
  }
}
