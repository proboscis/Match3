package com.glyph.scala.game.component

import com.glyph.scala.lib.engine.Entity
import com.glyph.scala.game.dungeon.{DungeonManager, TurnManager, TurnProcessor}

/**
 * @author glyph
 */
class DungeonActor(owner :Entity) extends TurnProcessor{
  lazy val transform = owner.get[DTransform]
  var dungeon :DungeonManager = null
  def setDungeon(d:DungeonManager){
    dungeon = d
  }
  
  def onMovePhase(manager: TurnManager) {}

  def onActionPhase(manager: TurnManager) {}

  def getPosition(): Int = transform.position

  //TODO:移動等のステートを誰がもつかについて検討
  def tryMove(dir:Int){
  }
}
