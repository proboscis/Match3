package com.glyph.scala.game.component

import com.glyph.scala.lib.engine.Entity

/**
 * @author glyph
 */
class Renderer(val owner: Entity, val delegate: AbstractRenderer) {
  delegate.initialize(this)
}
