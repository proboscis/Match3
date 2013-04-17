package com.glyph.scala.game.system

import com.glyph.scala.lib.entity_component_system.{GameContext, Entity, GameSystem}
import com.glyph.scala.game.event.{EntityRemoved, EntityAdded}
import com.glyph.scala.game.component.Tag
import collection.mutable.ListBuffer

/**
 * @author glyph
 */
class TagSystem(game:GameContext) extends GameSystem (game){

  private val entityMap = collection.mutable.HashMap.empty[String, ListBuffer[Entity]]

  game.eventManager += onEntityAdd
  game.eventManager += onEntityRemoved

  def onEntityAdd(event: EntityAdded): Boolean = {
    event.entity.get[Tag].map {
      tag => {
        entityMap get tag.tag match {
          case Some(x) => x += event.entity
          case None => entityMap(tag.tag) = ListBuffer[Entity](event.entity)
        }
      }
    }
    false
  }

  def onEntityRemoved(event: EntityRemoved): Boolean = {
    val tag = event.entity.directGet[Tag]
    if (tag != null) {
      entityMap get tag.tag match{
        case Some(x) =>{
          x -= event.entity
          if(x.size == 0){
            //make list garbage
            entityMap -= tag.tag
          }
        }
        case None =>
      }
    }
    false
  }


  def findEntity(tag: String): Option[Entity] = {
    val result = entityMap get tag match{
      case Some(list) => Option(list.head)
      case None => None
    }
    result
  }

  def findEntities(tag:String):Option[Seq[Entity]]={
    entityMap get tag
  }

  override def dispose() {
    super.dispose()
    game.eventManager -= onEntityAdd
    game.eventManager -= onEntityRemoved
  }
}

