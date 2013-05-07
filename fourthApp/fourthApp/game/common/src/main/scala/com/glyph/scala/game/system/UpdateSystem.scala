package com.glyph.scala.game.system

import com.glyph.scala.game.event.{EntityRemoved, EntityAdded}
import com.glyph.scala.lib.engine.{GameContext, EntityPackage, Entity}
import com.glyph.scala.game.component.update.Update
import com.glyph.scala.lib.util.LinkedList
import com.glyph.scala.lib.util.update.Updatable

/**
 * Updates all Update in the Entities
 * @author glyph
 */
class UpdateSystem(world:WorldSystem) extends Updatable{
  def update(delta:Float){
    val e = new Update(delta)
    world.entities.foreach {_.dispatch(e)}
  }
}
