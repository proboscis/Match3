package com.glyph.scala.game.adapter

import com.glyph.scala.lib.entity_component_system.Receptor
import com.glyph.scala.game.component.{Tag, Transform}

/**
 * @author glyph
 */
class PlayerAdapter {
  @Receptor
  val transform :Transform = null
  @Receptor
  val tag:Tag = null
}
