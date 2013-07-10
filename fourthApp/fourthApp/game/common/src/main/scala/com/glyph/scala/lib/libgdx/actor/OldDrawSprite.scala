package com.glyph.scala.lib.libgdx.actor

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.graphics.g2d.{Sprite, SpriteBatch}
import com.glyph.scala.lib.math.Vec2

/**
 * @author glyph
 */
trait OldDrawSprite extends Actor {
  val sprite: Sprite
  val offset = new Vec2
  override def draw(batch: SpriteBatch, parentAlpha: Float) {
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
