package com.glyph.scala.game.component

import com.glyph.scala.lib.entity_component_system.{Entity, Component}
import com.glyph.scala.lib.entity_component_system.math.Vec2


/**
 * Created with IntelliJ IDEA.
 * User: glyph
 * Date: 13/04/03
 * Time: 0:19
 * To change this template use File | Settings | File Templates.
 */
class Transform extends Component{
  val position = new Vec2
  val direction = new Vec2
}
