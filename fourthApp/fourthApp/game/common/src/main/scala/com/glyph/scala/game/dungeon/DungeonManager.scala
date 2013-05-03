package com.glyph.scala.game.dungeon

import com.glyph.scala.game.GameContext
import com.glyph.scala.lib.engine.EntityPackage
import com.glyph.scala.game.event.{EntityRemoved, EntityAdded}
import collection.mutable.ListBuffer
import com.glyph.scala.game.system.EntitySystem
import com.glyph.scala.game.component.DungeonActor

/**
 * @author glyph
 */
class DungeonManager(context:GameContext,pkg:EntityPackage)extends EntitySystem(context,pkg){
  val turnManager = new TurnManager
  val renderer = new DungeonRenderer(this)
  val map = Seq.fill(100)(1)
  val actors = new ListBuffer[DungeonActor]
  val iActor = pkg.getIndex[DungeonActor]

  def onAddEntity(e: EntityAdded): Boolean ={
    if(e.entity.hasI(iActor)){
      val actor = e.entity.getI[DungeonActor](iActor)
      actor.setDungeon(this)
      actors += actor
      turnManager.addProcessor(actor)
    }
    false
  }

  def tryMove(actor:DungeonActor,next:Int):Boolean ={
    if (map(next) == 0){
      false
    }else{
      //false を返したい
      actors.forall{
        a => (a ne actor) &&  a.getPosition() != next
      }
    }
  }

  def onRemoveEntity(e: EntityRemoved): Boolean = {
    if(e.entity.hasI(iActor)){
      val actor = e.entity.getI[DungeonActor](iActor)
      actors -= actor
      turnManager.removeProcessor(actor)
    }
    false
  }
}
