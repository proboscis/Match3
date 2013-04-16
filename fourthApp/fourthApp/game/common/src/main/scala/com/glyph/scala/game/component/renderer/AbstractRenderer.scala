package com.glyph.scala.game.component.renderer

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.glyph.scala.game.component.Transform
import com.glyph.scala.lib.entity_component_system.Entity

/**
 * @author glyph
 */
abstract class AbstractRenderer{
  var owner:Entity = null
  def draw(t:Transform,batch:SpriteBatch,alpha:Float)
  def initialize(owner:Entity){
    this.owner = owner
  }
}
