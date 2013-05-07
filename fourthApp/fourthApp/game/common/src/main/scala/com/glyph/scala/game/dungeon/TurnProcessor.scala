package com.glyph.scala.game.dungeon

import com.glyph.scala.lib.util.callback.Callback

/**
 * @author glyph
 */
trait TurnProcessor extends Callback{
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

object TurnProcessor{
  final val ACTION_END = 0
  final val MOVE_END = 1
}