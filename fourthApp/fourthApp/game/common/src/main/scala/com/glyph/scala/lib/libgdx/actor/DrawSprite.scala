package com.glyph.scala.lib.libgdx.actor

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.graphics.g2d.{Sprite, SpriteBatch}

/**
 * @author glyph
 */
trait DrawSprite extends Actor {
  val sprite: Sprite

  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    super.draw(batch, parentAlpha)
    sprite.setSize(getWidth, getHeight)
    sprite.setOrigin(getOriginX,getOriginY)
    sprite.setPosition(getX, getY)
    sprite.setScale(getScaleX, getScaleY)
    sprite.setRotation(getRotation)
    sprite.draw(batch, parentAlpha)
  }
}
