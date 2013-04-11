package com.glyph.scala.game.renderer

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.glyph.scala.game.component.GameActor

/**
 * @author glyph
 */
trait RendererTrait {
  def draw(actor:GameActor,batch:SpriteBatch,alpha:Float)
}
