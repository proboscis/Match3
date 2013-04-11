package com.glyph.scala.game.renderer

import com.badlogic.gdx.graphics.g2d.{Sprite, SpriteBatch}
import com.glyph.libgdx.asset.AM
import com.badlogic.gdx.graphics.Texture
import com.glyph.scala.game.component.Transform

/**
 * @author glyph
 */
class SimpleRenderer extends RendererTrait{
  val sprite = new Sprite(AM.instance().get[Texture]("data/skeleton.png"))
  override def draw(t:Transform, batch: SpriteBatch, alpha: Float) {
    sprite.setPosition(t.position.x,t.position.y)
    sprite.draw(batch,alpha)
  }
}
