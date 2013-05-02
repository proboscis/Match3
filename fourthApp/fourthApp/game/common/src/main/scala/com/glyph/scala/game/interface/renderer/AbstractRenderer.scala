package com.glyph.scala.game.interface.renderer

import com.badlogic.gdx.scenes.scene2d.Actor

/**
 * @author glyph
 */
abstract class AbstractRenderer extends Actor {
  var renderer: Renderer = null

  def initialize(renderer: Renderer) {
    this.renderer = renderer
  }
}
