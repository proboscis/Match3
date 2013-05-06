package com.glyph.scala.game.dungeon

/**
 * @author glyph
 */
trait TurnProcessor {
  /**
   * this is called when you are to move
   */
  def onMovePhase()

  /**
   * this is called when you can do action
   */
  def onActionPhase()

  /**
   * position in the dungeon
   * @return
   */
  def getPosition():Int
}
