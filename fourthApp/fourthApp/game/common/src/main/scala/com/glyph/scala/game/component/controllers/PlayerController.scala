package com.glyph.scala.game.component.controllers

import com.glyph.scala.lib.entity_component_system.Entity
import com.glyph.scala.game.event.UIInputEvent
import com.glyph.scala.lib.math.Vec2

/**
 * @author glyph
 */
class PlayerController extends AbstractController {
  override def initialize(owner: Entity) {
    super.initialize(owner)
    owner.game.eventManager += inputCallback
  }


  override def dispose(owner: Entity) {
    super.dispose(owner)
    owner.game.eventManager -= inputCallback
  }

  def inputCallback(event: UIInputEvent): Boolean = {
    event.typ match {
      case UIInputEvent.RIGHT_BUTTON => transform.position += Vec2.tmp.set(1, 0)
      case UIInputEvent.LEFT_BUTTON => transform.position += Vec2.tmp.set(-1, 0)
      case UIInputEvent.EXEC_BUTTON =>
      case _ => //impossible
    }
    true
  }
}
