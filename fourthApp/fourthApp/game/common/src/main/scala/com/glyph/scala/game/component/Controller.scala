package com.glyph.scala.game.component

import com.glyph.scala.lib.entity_component_system.{Entity, Component}
import com.glyph.scala.game.event.UIInputEvent
import com.glyph.scala.Glyph

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
    owner.game.eventManager += inputCallback
  }
  def inputCallback(event:UIInputEvent):Boolean = {
    transform.position += 1
    Glyph.log("handle button event")
    true
  }
}
