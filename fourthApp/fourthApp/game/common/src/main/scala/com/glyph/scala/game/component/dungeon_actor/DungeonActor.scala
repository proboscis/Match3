package com.glyph.scala.game.component.dungeon_actor

import com.glyph.scala.lib.engine.Entity
import com.glyph.scala.game.dungeon.{DungeonManager,TurnProcessor}
import com.glyph.scala.game.component.value.{Transform, DTransform}
import com.glyph.scala.Glyph

/**
 * you have to instantiate this class with some trait that implements the required methods.
 * @author glyph
 */
abstract class DungeonActor(protected val owner: Entity) extends TurnProcessor{
  import DungeonActor._
  import Direction._
  lazy val dTransform = owner.get[DTransform]
  lazy val transform = owner.get[Transform]
  var dungeon: DungeonManager = null
  val log = Glyph.log("DungeonActor")_
  def setDungeon(d: DungeonManager) {
    dungeon = d
  }


  def onMovePhase() {
    owner.dispatch(new DungeonActor.onMovePhase)
  }

  def onActionPhase() {
    owner.dispatch(new DungeonActor.onActionPhase)
  }

  def getPosition(): Int = dTransform.position

  def tryMove(dir: Direction){
    //TODO 複数回行動の実装
    onMoveEnd()
  }
  def doAction(){
    log("doAction")
    //TODO 複数回行動の実装
    onActionEnd()
  }
}

object DungeonActor {

  object Direction extends Enumeration {
    type Direction = Value
    val RIGHT, LEFT, UP, DOWN = Value
  }
  class onMovePhase
  class onActionPhase

}
