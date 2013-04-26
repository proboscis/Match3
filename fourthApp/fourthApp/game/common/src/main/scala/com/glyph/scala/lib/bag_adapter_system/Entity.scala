package com.glyph.scala.lib.bag_adapter_system

import com.glyph.scala.lib.util.LinkedSets

/**
 * @author glyph
 */
class Entity {
  val members = new LinkedSets[String,Any]
  val interfaces = new LinkedSets[String,Interface]
  def addMember(name:String,value:Any){
    members.push(name,value)
  }
  def findMember[T](name:String):T={
    members.find(name).asInstanceOf[T]
  }
  def addInterface[T<:Interface](name:String,value:T){
    interfaces.push(name,value)
    value.initialize(this)
  }
  def findInterface[T<:Interface](name:String):T={
    interfaces.find(name).asInstanceOf[T]
  }
}
