package com.glyph.scala.lib.entity_game_system

import com.glyph.scala.lib.util.ArrayMap

/**
 * @author glyph
 */
class EntityFactory(pack:EntityPackage){
  private val constructors = new ArrayMap[String, () => Entity]
  def addConstructor(name:String)(proc: ()=> Entity) ={
    println(proc)
    constructors.set(name,proc)
  }
  def getConstructor(name:String):()=>Entity={
    constructors.get(name)
  }
  def construct(f:String):Entity={
    constructors.get(f)()
  }
}
