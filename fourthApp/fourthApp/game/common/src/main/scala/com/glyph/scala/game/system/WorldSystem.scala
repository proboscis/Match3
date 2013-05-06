package com.glyph.scala.game.system

import com.glyph.scala.game.GameContext
import com.glyph.scala.lib.engine.{Entity, EntityPackage}
import com.glyph.scala.game.event.{EntityRemoved, EntityAdded}
import com.glyph.scala.lib.util.LinkedList

/**
 * system that holds all the entities
 * @author glyph
 */
class WorldSystem(context:GameContext,pkg:EntityPackage) extends EntitySystem(context){
  private val mEntities = new LinkedList[Entity]
  def entities:Traversable[Entity] = mEntities
  def onAddEntity(e: EntityAdded): Boolean ={
    if (e.entity.pkg == pkg){
      mEntities.push(e.entity)
    }
    false
  }

  def onRemoveEntity(e: EntityRemoved): Boolean = {
    if (e.entity.pkg == pkg){
      mEntities.remove(e.entity)
    }
    false
  }
}
