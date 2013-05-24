package com.glyph.scala.lib.util.actor

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.graphics.g2d.{Sprite, SpriteBatch}
import com.glyph.java.asset.AM
import com.badlogic.gdx.graphics.Texture

/**
 * @author glyph
 */
trait Debug extends Actor{
  val sprite = new Sprite(AM.instance().get[Texture]("data/lightbulb32.png"))
  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    super.draw(batch, parentAlpha)
    sprite.setPosition(getX,getY)
    sprite.setSize(getWidth,getHeight)
    sprite.setScale(getScaleX,getScaleY)
    sprite.setRotation(getRotation)
    sprite.draw(batch,parentAlpha)
  }
}
