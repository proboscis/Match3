package com.glyph.scala.game.component.controllers

import com.glyph.scala.lib.entity_component_system.{Entity, Component}

/**
 * @author glyph
 */
class Controller(val controller: AbstractController) extends Component {
  override def initialize(owner: Entity) {
    super.initialize(owner)
    controller.initialize(owner)
  }

  override def finish(owner: Entity) {
    super.finish(owner)
    controller.dispose(owner)
  }
}

