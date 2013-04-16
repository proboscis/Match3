package com.glyph.scala.game.component.renderer

import com.glyph.scala.lib.entity_component_system.{Entity, Component}
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.glyph.scala.game.component.Transform
import com.glyph.scala.game.component.renderer.SimpleRenderer
import com.glyph.scala.Glyph

/**
 * @author glyph
 */
class Renderer (private val renderer:AbstractRenderer)extends Component{
 // Glyph.log("renderer class",""+Manifest.classType(this.getClass).runtimeClass)
  private var transform: Transform = null
  override def initialize(owner: Entity) {
    super.initialize(owner)
    transform = owner.directGet[Transform]
    renderer.initialize(owner)
  }

  def draw(batch:SpriteBatch,alpha:Float){
    renderer.draw(transform,batch,alpha)
  }
}
