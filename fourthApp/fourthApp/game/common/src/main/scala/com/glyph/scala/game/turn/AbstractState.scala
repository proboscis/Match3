package com.glyph.scala.game.turn

/**
 * @author glyph
 */
abstract class AbstractState(manager:TurnManager) {
  def start()
  def onTurnEnd(p:TurnProcessor)
  def init()
}
