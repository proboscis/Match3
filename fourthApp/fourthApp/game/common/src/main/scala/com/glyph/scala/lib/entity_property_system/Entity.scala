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
    mIndex = -1
    mWorld = null
  }

  def index = mIndex
  def world = mWorld

  def addComponent[T<:Component](component:T){
    mWorld.componentManager.addComponent(this,component)
  }
  def removeComponent[T<:Component](component:T){
    mWorld.componentManager.removeComponent(this,component)
  }
  def notifyChanged(){
    mWorld.componentManager.notifyChanged(this)
  }
}
