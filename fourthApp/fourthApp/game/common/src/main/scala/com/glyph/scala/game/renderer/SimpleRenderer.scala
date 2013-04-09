package com.glyph.scala.game.renderer

import com.badlogic.gdx.graphics.g2d.{SpriteBatch, Sprite}
import com.badlogic.gdx.graphics.Texture
import com.glyph.libgdx.asset.AM
import com.glyph.scala.Glyph

/**
 * Created with IntelliJ IDEA.
 * User: glyph
 * Date: 13/04/08
 * Time: 19:09
 * To change this template use File | Settings | File Templates.
 */
class SimpleRenderer extends Renderer{
  val sprite = new Sprite(AM.instance().get[Texture]("data/skeleton.png"))
  def render(batch: SpriteBatch,alpha:Float, component: RendererComponent) {
    val pos = component.transform.position
    sprite.setPosition(pos.x,pos.y)
    sprite.draw(batch,alpha)
  }
}
