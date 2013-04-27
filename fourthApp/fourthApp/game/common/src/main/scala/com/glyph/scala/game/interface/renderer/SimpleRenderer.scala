package com.glyph.scala.game.interface.renderer

import com.badlogic.gdx.graphics.g2d.{SpriteBatch, Sprite}
import com.glyph.libgdx.asset.AM
import com.badlogic.gdx.graphics.Texture
import com.glyph.scala.game.component.Transform

/**
 * @author glyph
 */
class SimpleRenderer extends AbstractRenderer{
  lazy val sprite = new Sprite(AM.instance().get[Texture]("data/skeleton.png"))
  lazy val transform = renderer.owner.getMember[Transform]
  def draw(batch: SpriteBatch, parentAlpha: Float) {
    sprite.setPosition(transform.position.x,transform.position.y)
    sprite.setRotation(transform.direction.angle())
    sprite.draw(batch,parentAlpha)
  }
}
