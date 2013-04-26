package com.glyph.scala.lib.entity_game_system

import com.glyph.scala.lib.dynamic_type_system.DynamicInstance
import com.glyph.scala.lib.util.laz.Lazy

/**
 * @author glyph
 */
class Entity(pkg:EntityPackage, pool: EntityPool) {
  val members = new DynamicInstance(pkg.memberClass)
  val interfaces = new DynamicInstance(pkg.interfaceClass)
  def free() {
    members.members.clear()
    members.memberBits.clear()
    interfaces.members.clear()
    interfaces.memberBits.clear()
    pool.addFreed(this)
  }

  def setInterface[T<:EntityInterface:Manifest](name:String,interface:T){
    interfaces.set(name,new Lazy[T,Entity](interface))
  }
  def getInterface[T<:EntityInterface:Manifest](name:String){
    interfaces.get[Lazy[T,Entity]](name).initIfNeeded(this)
  }

  def setInterfaceUnchecked[T<:EntityInterface](index:Int,interface:T){
    interfaces.setUnchecked(index,new Lazy[T,Entity](interface))
  }
  def getInterfaceUnchecked[T<:EntityInterface](index:Int){
    interfaces.getUnchecked[Lazy[T,Entity]](index).initIfNeeded(this)
  }


  def get[T:Manifest](name:String){
    members.get[T](name)
  }
  def set[T:Manifest](name:String,value:T){
    members.set[T](name,value)
  }
  def getUnchecked[T:Manifest](index:Int){
    members.getUnchecked[T](index)
  }
  def setUnchecked[T:Manifest](index:Int,value:T){
    members.setUnchecked[T](index,value)
  }
}
