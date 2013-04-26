package com.glyph.scala.lib.entity_property_system

import java.util
import com.glyph.scala.lib.util.{DeprecatedPoolable, Chainable}

/**
 * @author glyph
 */
class Entity extends DeprecatedPoolable with Chainable{
  private var mIndex = -1
  private var mWorld :World = null
  val componentBits = new util.BitSet()
  def init(index: Int, world: World){
    mWorld = world
    mIndex = index
  }
  def free() {
    removeAllComponent()
    mIndex = -1
    mWorld = null
    componentBits.clear()
  }

  def index = mIndex
  def world = mWorld

  def delete(){
    mWorld.entityFactory.deleteEntity(this)
  }

  def modify(edit:Entity =>Unit):Entity ={
    edit(this)
    notifyChanged()
    this
  }

  def hasAllComponents(filter:util.BitSet):Boolean={
    val bits = componentBits
    var i = filter.nextSetBit(0)
    var hasAllComponent = true// check this is not true
    while (i >= 0) {
      if (bits.get(i)){
        hasAllComponent = hasAllComponent && true
      }else{
        hasAllComponent = false
      }
      i = filter.nextSetBit(i + 1)
    }
    hasAllComponent
  }

  def addComponent[T<:Component](component:T)(implicit typ:Manifest[T])={
    mWorld.entityFactory.componentManager.addComponent(this,component)
  }
  def addComponent[T<:Component](component:T,componentIndex:Int)(implicit typ:Manifest[T])={
    mWorld.entityFactory.componentManager.addComponent(this,componentIndex,component)
  }
  def removeComponent[T<:Component](component:T)(implicit typ:Manifest[T])={
    mWorld.entityFactory.componentManager.removeComponent(this,component)
  }
  def notifyChanged(){
    mWorld.entityFactory.componentManager.notifyChanged(this)
  }
  def removeAllComponent(){
    mWorld.entityFactory.componentManager.removeAllComponent(this)
  }
}
