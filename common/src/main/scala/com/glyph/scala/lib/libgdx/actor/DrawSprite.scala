package com.glyph.scala.lib.libgdx.actor

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.graphics.g2d.{Batch, Sprite}
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2

trait DrawSprite extends Actor {
  def drawSprite(batch: Batch, sprite: Sprite, parentAlpha: Float, offset: Vector2 = null, color: Color = getColor) {
    sprite.setSize(getWidth, getHeight)
    sprite.setOrigin(getOriginX, getOriginY)
    if (offset != null) {
      sprite.setPosition(getX + offset.x, getY + offset.y)
    } else {
      sprite.setPosition(getX, getY)
    }
    sprite.setScale(getScaleX, getScaleY)
    sprite.setRotation(getRotation)
    sprite.setColor(color)
    sprite.draw(batch, parentAlpha)
  }
}