package com.glyph.scala.game.component

import com.glyph.scala.lib.entity_component_system.{Entity, Component}
import com.glyph.scala.game.event.ProcessTurn
import com.glyph.scala.game.traits.EntityEventReceiver

/**
 * @author glyph
 */
class DungeonGame extends Component with EntityEventReceiver{
  //this component should have its state or something like that
  val dungeonMap = List(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)

  override def initialize(owner: Entity) {
    super.initialize(owner)
    owner.game.eventManager += processTurn
  }


  override def finish(owner: Entity) {
    super.finish(owner)
    owner.game.eventManager -= processTurn
  }

  def processTurn(event: ProcessTurn):Boolean = {
    false
  }
}
