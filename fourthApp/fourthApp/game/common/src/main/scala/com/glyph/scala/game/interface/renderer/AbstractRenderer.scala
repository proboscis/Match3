package com.glyph.scala.game.interface.renderer

import com.badlogic.gdx.graphics.g2d.SpriteBatch

/**
 * @author glyph
 */
abstract class AbstractRenderer {
  var renderer: Renderer = null
  def initialize(renderer:Renderer){
    this.renderer = renderer
  }
  def draw(batch:SpriteBatch,parentAlpha:Float)
}
