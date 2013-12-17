package com.glyph.scala.lib.libgdx.actor

import com.badlogic.gdx.graphics.g2d.{TextureRegion, SpriteBatch, Sprite}

/**
 * @author glyph
 */
class SpriteActor extends DrawSprite{
  val sprite = new Sprite

  def reset() {
    sprite.setTexture(null)
    clear()
    remove()
  }



  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    super.draw(batch, parentAlpha)
    drawSprite(batch, sprite, parentAlpha)
  }
}

object SpriteActor {
  def apply(region: TextureRegion): SpriteActor = {
    val sp = new SpriteActor
    sp.sprite.setRegion(region)
    sp
  }
}
