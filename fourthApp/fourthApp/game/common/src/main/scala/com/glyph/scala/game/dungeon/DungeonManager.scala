package com.glyph.scala.game.dungeon

import animation.AnimationManager
import com.glyph.scala.game.GameContext
import com.glyph.scala.lib.engine.EntityPackage
import com.glyph.scala.game.event.{EntityRemoved, EntityAdded}
import collection.mutable.ListBuffer
import com.glyph.scala.game.system.EntitySystem
import com.glyph.scala.game.component.dungeon_actor.DungeonActor
import com.glyph.scala.lib.util.update.Updatable

/**
 * @author glyph
 */
class DungeonManager(context:GameContext,pkg:EntityPackage)extends EntitySystem(context) with Updatable{
  val animationManager = new AnimationManager
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
      //println(next)
      //false を返したい
      actors.forall{
        a => a.getPosition() != next
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

  def update(delta: Float) {
    animationManager.update(delta)
  }
}
