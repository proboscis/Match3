package com.glyph.scala.game.component

import com.glyph.scala.lib.entity_component_system.{Entity, Component}
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.glyph.scala.game.renderer.SimpleRenderer

/**
 * @author glyph
 */
class Renderer extends Component{
  private var transform: Transform = null
  private val renderer = new SimpleRenderer
  override def initialize(owner: Entity) {
    super.initialize(owner)
    transform = owner.get[Transform]
  }

  def draw(batch:SpriteBatch,alpha:Float){
    renderer.draw(transform,batch,alpha)
  }
}
