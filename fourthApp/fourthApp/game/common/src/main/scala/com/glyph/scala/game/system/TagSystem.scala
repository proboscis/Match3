package com.glyph.scala.game.system

import com.glyph.scala.lib.entity_component_system.{Entity, GameSystem}
import com.glyph.scala.game.event.{EntityRemoved, EntityAdded}
import com.glyph.scala.game.component.Tag
import collection.mutable.ListBuffer

/**
 * @author glyph
 */
class TagSystem extends GameSystem {

  private val entityMap = collection.mutable.HashMap.empty[String, ListBuffer[Entity]]

  def onEntityAdd(event: EntityAdded): Boolean = {
    val tag = event.entity.get[Tag]
    if (tag != null) {
      entityMap get tag.tag match {
        case Some(x) => x += event.entity
        case None => entityMap(tag.tag) = ListBuffer[Entity](event.entity)
      }
    }
    false
  }

  def onEntityRemoved(event: EntityRemoved): Boolean = {
    val tag = event.entity.get[Tag]
    if (tag != null) {
      entityMap(tag.tag) = null
    }
    false
  }


  def findEntity(tag: String): Option[Entity] = {
    val list = entityMap(tag)
    if (list != null) {
      return Option(list.head)
    } else {
      return Option(null)
    }
  }

  override def dispose() {
    super.dispose()
  }
}

