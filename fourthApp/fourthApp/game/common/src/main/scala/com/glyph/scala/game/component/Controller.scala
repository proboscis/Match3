package com.glyph.scala.game.component

import com.glyph.scala.lib.entity_component_system.{Entity, Component}

/**
 * @author glyph
 */
class Controller extends Component{
  var transform :Transform = null
  var dungeonActor: DungeonActor = null
  override def initialize(owner: Entity) {
    super.initialize(owner)
    transform = owner.get[Transform]
    dungeonActor = owner.get[DungeonActor]
  }
}
