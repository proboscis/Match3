package com.glyph.scala.game.component

import com.glyph.scala.lib.entity_component_system.Component
import com.glyph.scala.game.traits.EntityEventReceiver

/**
 * @author glyph
 */
class DungeonGame extends Component with EntityEventReceiver{
  //this component should have its state or something like that
  val dungeonMap = Array(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)

}
