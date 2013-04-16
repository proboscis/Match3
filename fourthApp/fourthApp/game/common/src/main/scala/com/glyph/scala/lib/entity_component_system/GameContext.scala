package com.glyph.scala.lib.entity_component_system

import com.glyph.scala.lib.event.EventManager


class GameContext {
  val eventManager = new EventManager
  val entityManager = new EntityManager(this)
  val systemManager = new SystemManager(this)

  def update(delta:Float){
    systemManager.update(delta)
  }

  def dispose(){
    systemManager.dispose()
  }
}

