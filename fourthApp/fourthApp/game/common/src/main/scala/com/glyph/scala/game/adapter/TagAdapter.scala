package com.glyph.scala.game.adapter

import com.glyph.scala.lib.entity_component_system.{Entity, Adapter, Receptor}
import com.glyph.scala.game.component.Tag

/**
 * @author glyph
 */
class TagAdapter(e:Entity) extends Adapter(e){
  @Receptor
  val tag :Tag = null
}
