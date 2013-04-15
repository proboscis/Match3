package com.glyph.scala.game.traits

import com.glyph.scala.lib.entity_component_system.{Entity, Component}
import com.glyph.scala.game.event.{EntityRemoved, EntityAdded}


/**
 * @author glyph
 */
trait EntityEventReceiver extends Component{
  //self: Component =>
  override def initialize(owner: Entity) {
    super.finish(owner)
    owner.game.eventManager += onEntityAdded
    owner.game.eventManager += onEntityRemoved
  }


  override def finish(owner: Entity) {
    super.finish(owner)
    owner.game.eventManager -= onEntityAdded
    owner.game.eventManager -= onEntityRemoved
  }

  def onEntityAdded(event:EntityAdded):Boolean={
    false
  }
  def onEntityRemoved(event:EntityRemoved):Boolean={
    false
  }

}
