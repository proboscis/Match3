package com.glyph.scala.game.interface.renderer

import com.glyph.scala.lib.engine.Interface
/**
 * @author glyph
 */
class Renderer(val delegate:AbstractRenderer)extends  Interface{
  delegate.initialize(this)
}
