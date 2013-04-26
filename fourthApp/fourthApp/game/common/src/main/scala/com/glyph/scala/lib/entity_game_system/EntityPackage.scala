package com.glyph.scala.lib.entity_game_system

import com.glyph.scala.lib.dynamic_type_system.DynamicClass
import com.glyph.scala.lib.util.TypeCheckedMap
import com.glyph.scala.lib.event.EventManager

/**
 * @author glyph
 */
class EntityPackage(val pkgName:String,val game:GameContext)extends TypeCheckedMap[String,Any]{
  val eventManager = new EventManager()
  game.eventManager.addChild(eventManager)
  val memberClass = new DynamicClass(pkgName+".member")
  val interfaceClass = new DynamicClass(pkgName+".interface")
  private val entityManager = new EntityManager
  val factory = new EntityFactory(this)
  private val pool = new EntityPool(this)

  def obtain(): Entity = pool.obtain()
  def register(e: Entity) ={
    entityManager.addEntity(e)
  }
  def unregister(e: Entity) = entityManager.removeEntity(e)
}
