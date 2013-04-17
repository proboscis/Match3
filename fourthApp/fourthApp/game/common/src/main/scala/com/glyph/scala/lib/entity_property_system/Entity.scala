package com.glyph.scala.lib.entity_property_system

/**
 * @author glyph
 */
class Entity extends Poolable{
  private var mIndex = -1
  private var mWorld :World = null
  def init(index: Int, world: World){
    this mWorld = world
    this.mIndex = index
  }
  def free() {
    removeAllComponent()
    mIndex = -1
    mWorld = null
  }

  def index = mIndex
  def world = mWorld

  def addComponent[T<:Component](component:T)(implicit typ:Manifest[T])={
    mWorld.entityManager.componentManager.addComponent(this,component)
  }
  def removeComponent[T<:Component](component:T)(implicit typ:Manifest[T])={
    mWorld.entityManager.componentManager.removeComponent(this,component)
  }
  def notifyChanged(){
    mWorld.entityManager.componentManager.notifyChanged(this)
  }
  def removeAllComponent(){
    mWorld.entityManager.componentManager.removeAllComponent(this)
  }
}
