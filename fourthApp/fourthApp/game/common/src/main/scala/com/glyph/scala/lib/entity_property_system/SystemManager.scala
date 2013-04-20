package com.glyph.scala.lib.entity_property_system

import java.util
import com.glyph.libgdx.util.ArrayStack
import com.glyph.scala.Glyph

/**
 * @author glyph
 */
class SystemManager(world: World) {
  val DEBUG = true
  val mSystems = new ArrayStack[GameSystem]()
  val mSystemIndexMap = new util.HashMap[Manifest[_], Int]
  val log = (msg: String) => {
    if (DEBUG) Glyph.log("SystemManager", msg)
  }


  def onAddEntity(e:Entity){
    var i = 0;
    while(i < mSystems.size()){
      val system = mSystems.get(i)
      system.onAddEntity(e)
      i+= 1
    }
  }
  def onRemoveEntity(e:Entity){
    var i = 0;
    while(i < mSystems.size()){
      val system = mSystems.get(i)
      system.onRemoveEntity(e)
      i+= 1
    }
  }


  def getSystemIndex[T <: GameSystem](implicit typ: Manifest[T]): Int = {
    mSystemIndexMap.get(typ)
  }

  def registerSystem[T <: GameSystem](system: T)(implicit typ: Manifest[T]) {
    if (!mSystems.contains(system)) {
      mSystems.push(system)
      mSystemIndexMap.put(typ, mSystems.size() - 1)
      system.onAddedToWorld(world)
    }
  }

  def findSystem[T <: GameSystem](implicit typ: Manifest[T]): Option[T] = {
    Option(mSystems.get(getSystemIndex[T]).asInstanceOf[T])
  }

  def update(delta: Float) {
    var i = 0;
    while(i < mSystems.size()){
      val system = mSystems.get(i)
      if(system.isEnabled){
        system.update(delta)
      }
      i+= 1
    }
  }
}
