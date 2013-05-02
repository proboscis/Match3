package com.glyph.scala.game.dungeon

import com.glyph.scala.game.turn.TurnProcessor

/**
 * @author glyph
 */
trait DungeonActor extends TurnProcessor{
  def onMove(){}
  def onAction(){}
}
