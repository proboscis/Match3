package com.glyph.scala.game.system

import com.glyph.scala.lib.entity_component_system.{Entity, GameContext, GameSystem}
import com.glyph.scala.game.event.UIInputEvent
import com.glyph.scala.Glyph

/**
 * @author glyph
 */
class PlayerControlSystem(game: GameContext) extends GameSystem {

  game.eventManager += onPlayerInput

  var player: Entity = null

  def onPlayerInput(event: UIInputEvent): Boolean = {
    true
  }


  override def update(delta: Float) {
    super.update(delta)
    if (player == null) {
      //player = game.systemManager.getSystem[TagSystem].findEntity("player")
      if (player == null){
       // Glyph.log("cannot find ")
      }
    }
  }

  override def dispose() {
    super.dispose()
    game.eventManager -= onPlayerInput
  }
}
