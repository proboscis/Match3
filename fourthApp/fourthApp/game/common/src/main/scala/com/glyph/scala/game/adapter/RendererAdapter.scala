package com.glyph.scala.game.adapter

import com.glyph.scala.lib.entity_component_system.{Entity, Adapter, Receptor}
import com.glyph.scala.game.component.renderer.Renderer

/**
 * @author glyph
 */
class RendererAdapter(e:Entity) extends Adapter(e){
  @Receptor
  val renderer:Renderer = null
}

