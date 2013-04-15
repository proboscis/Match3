package com.glyph.scala.game.system

import com.glyph.scala.lib.entity_component_system._
import com.glyph.scala.game.event.UIInputEvent
import com.glyph.scala.Glyph
import com.glyph.scala.game.component.controllers.Controller

/**
 * @author glyph
 */
class ControllerSystem(game: GameContext) extends GameSystem {
  game.entityManager.addAdapter[ControllerAdapter]

  override def update(delta: Float) {
    super.update(delta)
    game.entityManager.getAdapters[ControllerAdapter].foreach {
      _.controller.controller.update(delta)
    }
  }

  override def dispose() {
    super.dispose()
    game.entityManager.removeAdapter[ControllerAdapter]
  }
}


class ControllerAdapter(e: Entity) extends Adapter(e) {
  @Receptor
  val controller: Controller = null
}