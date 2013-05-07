package com.glyph.scala.game.dungeon

import animation.AnimationManager
import com.glyph.scala.lib.engine.{GameContext, EntityPackage}
import com.glyph.scala.game.event.{EntityRemoved, EntityAdded}
import collection.mutable.ListBuffer
import com.glyph.scala.game.system.EntitySystem
import com.glyph.scala.game.component.dungeon_actor.DungeonActor
import com.glyph.scala.lib.util.update.Updatable
import com.glyph.scala.Glyph

/**
 * @author glyph
 */
class DungeonManager(context:GameContext,pkg:EntityPackage)extends EntitySystem(context) with Updatable{
  val animationManager = new AnimationManager
  private val turnManager = new TurnManager
  val renderer = new DungeonRenderer(this)
  val map = Seq.fill(100)(1)
  val actors = new ListBuffer[DungeonActor]
  val iActor = pkg.getIndex[DungeonActor]
  val log = Glyph.log("DungeonManager")_

  def setFocus(focus:DungeonActor){
    turnManager.setFocus(focus)
  }


  /**
   * animationManagerとturnManagerの接続処理・・・これはここで書くべきか否か？
   * やはり、書くべきではないだろう
   */
  turnManager.onFocusActionDone+={()=>{
    log("focus action done")
    animationManager.startSequential()
  }}
  turnManager.onFocusMoveDone+={()=>{
    log("focus move done")
    turnManager.phaseEnd()
  }}
  turnManager.onReactionDone+={()=>{
    log("reaction done")
    animationManager.startSequential()
  }}
  turnManager.onActionDone+={()=>{
    log("action done")
    animationManager.startSequential()
  }}
  turnManager.onMoveDone+={()=>{
    log("move done")
    animationManager.startParallel()
  }}
  animationManager.onParallelEnd+={()=>{
    log("parallel done")
    turnManager.phaseEnd()
  }}
  animationManager.onSequenceEnd +={()=>{
    log("sequential done")
    turnManager.phaseEnd()
  }}

  def onAddEntity(e: EntityAdded){
    if(e.entity.hasI(iActor)){
      val actor = e.entity.getI[DungeonActor](iActor)
      actor.setDungeon(this)
      actors += actor
      turnManager.addProcessor(actor)
    }
  }

  def tryMove(actor:DungeonActor,next:Int):Boolean ={
      //println(next)
      //false を返したい
      actors.forall{
        a => a.getPosition() != next
      }
  }

  def onRemoveEntity(e: EntityRemoved){
    if(e.entity.hasI(iActor)){
      val actor = e.entity.getI[DungeonActor](iActor)
      actors -= actor
      turnManager.removeProcessor(actor)
    }
  }

  def update(delta: Float) {
    animationManager.update(delta)
  }
}
