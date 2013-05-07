package com.glyph.scala.game.dungeon

import com.glyph.scala.lib.util.callback.Callback

/**
 * @author glyph
 */
trait TurnProcessor{
  lazy val onActionEnd = new Callback
  lazy val onMoveEnd = new Callback
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