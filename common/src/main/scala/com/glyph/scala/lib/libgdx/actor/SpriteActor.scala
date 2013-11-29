package com.glyph.scala.lib.libgdx.actor

import com.badlogic.gdx.graphics.g2d.{SpriteBatch, Sprite, TextureRegion}

/**
 * @author glyph
 */
class SpriteActor(val sprite: Sprite) extends DrawSprite {
  override def draw(batch: SpriteBatch, parentAlpha: Float){
    super.draw(batch,parentAlpha)
    drawSprite(batch,sprite,parentAlpha)
  }
}
