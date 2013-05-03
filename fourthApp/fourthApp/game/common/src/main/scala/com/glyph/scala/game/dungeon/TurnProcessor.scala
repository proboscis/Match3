package com.glyph.scala.game.dungeon

/**
 * @author glyph
 */
trait TurnProcessor {
  /**
   * call manager.turnEnd when you finished Moving
   * @param manager
   */
  def onMovePhase(manager:TurnManager)

  /**
   * call manager.turnEnd when you finished action
   * @param manager
   */
  def onActionPhase(manager:TurnManager)

  /**
   * position in the dungeon
   * @return
   */
  def getPosition():Int
}
