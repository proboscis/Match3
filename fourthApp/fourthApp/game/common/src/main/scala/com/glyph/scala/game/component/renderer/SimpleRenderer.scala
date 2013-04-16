package com.glyph.scala.game.component.renderer

import com.badlogic.gdx.graphics.g2d.{Sprite, SpriteBatch}
import com.glyph.libgdx.asset.AM
import com.badlogic.gdx.graphics.Texture
import com.glyph.scala.game.component.Transform
import com.glyph.scala.Glyph

/**
 * @author glyph
 */
class SimpleRenderer extends AbstractRenderer{
  val sprite = new Sprite(AM.instance().get[Texture]("data/skeleton.png"))
  def draw(t:Transform, batch: SpriteBatch, alpha: Float) {
    sprite.setPosition(t.position.x,t.position.y)
    sprite.draw(batch,alpha)
  }
}
