package com.glyph.scala.game.renderer

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.glyph.scala.game.component.Transform

/**
 * @author glyph
 */
trait RendererTrait {
  def draw(t:Transform,batch:SpriteBatch,alpha:Float)
}
