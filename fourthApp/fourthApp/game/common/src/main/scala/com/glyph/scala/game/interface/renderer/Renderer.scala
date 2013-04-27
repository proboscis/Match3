package com.glyph.scala.game.interface.renderer

import com.glyph.scala.lib.engine.Interface
import com.badlogic.gdx.graphics.g2d.SpriteBatch

/**
 * @author glyph
 */
class Renderer(delegate:AbstractRenderer)extends Interface{
  delegate.initialize(this)
  def draw(batch:SpriteBatch,alpha:Float){
    delegate.draw(batch,alpha)
  }
}
