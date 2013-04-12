package com.glyph.scala.game.component

import com.glyph.scala.lib.entity_component_system.Component
import com.glyph.scala.lib.entity_component_system.math.Vec2

/**
 * @author glyph
 */
class Transform extends Component{
  val position = new Vec2()
  val direction = new Vec2()
}
