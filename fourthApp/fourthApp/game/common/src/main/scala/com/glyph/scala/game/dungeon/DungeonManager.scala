package com.glyph.scala.game.dungeon

import com.glyph.scala.game.turn.{TurnProcessor, AbstractState, TurnManager}


/**
 * @author glyph
 */
class DungeonManager {
  val manager = new TurnManager
  def start(){
    manager.start()
  }
}
class Move(manager:TurnManager) extends AbstractState(manager){
  def start() {}

  def onTurnEnd(p: TurnProcessor) {}

  def init() {}
}
class Action(manager:TurnManager) extends AbstractState(manager){
  def start() {}

  def onTurnEnd(p: TurnProcessor) {}

  def init() {}
}