package com.glyph.scala.game.puzzle.model.monsters

import com.glyph.scala.game.puzzle.system.turn.TurnManager

/**
 * @author glyph
 */
class Slime extends Monster{
  /**
   * processors must have some pointer to the whole games...
   * do the puzzle must know of whole game? no!
   * only monsters want to know.
   * so why not make puzzle to be able to handle outer panels!
   * at least one must
   * @param manager TurnManager
   */
  def process(manager: TurnManager) {}
}
