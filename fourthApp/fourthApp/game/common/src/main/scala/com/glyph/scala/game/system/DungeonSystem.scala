package com.glyph.scala.game.system

import com.glyph.scala.game.GameContext
import com.glyph.scala.lib.engine.EntityPackage
import com.glyph.scala.game.event.{EntityRemoved, EntityAdded}
import collection.mutable.ListBuffer
import com.glyph.scala.game.interface.DungeonActor

/**
 * @author glyph
 */
class DungeonSystem(context:GameContext,pkg:EntityPackage)extends EntitySystem(context,pkg){
  val actors = new ListBuffer[DungeonActor]
  val iActor = pkg.getInterfaceIndex[DungeonActor]

  def onAddEntity(e: EntityAdded): Boolean ={
    if(e.entity.hasInterface(iActor)){
      actors += e.entity.getInterfaceI[DungeonActor](iActor)
    }
    false
  }

  def onRemoveEntity(e: EntityRemoved): Boolean = {
    if(e.entity.hasInterface(iActor)){
      actors -= e.entity.getInterfaceI[DungeonActor](iActor)
    }
    false
  }

}
