package com.glyph.scala.game.renderer

import com.badlogic.gdx.graphics.g2d.{Sprite, SpriteBatch}
import com.glyph.libgdx.asset.AM
import com.badlogic.gdx.graphics.Texture
import com.glyph.scala.game.component.GameActor

/**
 * @author glyph
 */
class SimpleRenderer extends RendererTrait{
  val sprite = new Sprite(AM.instance().get[Texture]("data/skeleton.png"))
  override def draw(actor:GameActor,batch: SpriteBatch, alpha: Float) {
    sprite.setSize(actor.getWidth,actor.getHeight)
    sprite.setOrigin(actor.getOriginX,actor.getOriginY)
    sprite.setRotation(actor.getRotation)
    sprite.draw(batch,alpha)

  }
}
