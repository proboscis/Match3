package com.glyph.scala.game.system

import com.glyph.scala.lib.entity_component_system.{GameContext, GameSystem}
import com.glyph.scala.game.event.ProcessTurn

/**
 * @author glyph
 */
class DungeonSystem(game: GameContext) extends GameSystem {
  game.eventManager += processTurn
  def processTurn(event: ProcessTurn): Boolean = {
    true
  }
  override def dispose() {
    super.dispose()
    game.eventManager -= processTurn
  }
}
