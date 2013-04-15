package com.glyph.scala.game.component

import com.glyph.scala.lib.entity_component_system.{Entity, Component}
import com.glyph.scala.game.event.{ProcessTurn, UIInputEvent}
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
    event.typ match{
      case UIInputEvent.RIGHT_BUTTON =>
      case UIInputEvent.LEFT_BUTTON=>
        transform.position += 1
      case UIInputEvent.EXEC_BUTTON=>
        owner.game.eventManager dispatch new ProcessTurn
    }
    true
  }
}
