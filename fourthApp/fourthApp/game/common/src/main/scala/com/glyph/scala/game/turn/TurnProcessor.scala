package com.glyph.scala.game.turn

/**
 * @author glyph
 */
trait TurnProcessor {
  var manager:TurnManager = null
  def setManager(manager:TurnManager){
    this.manager = manager
  }
  def startTurn(state:AbstractState)
  def endTurn(){
    manager.onTurnEnd(this)
  }
}
