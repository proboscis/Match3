package com.glyph.scala.lib.entity_component_system

import com.glyph.scala.lib.entity_component_system.GameContext

class Entity {
  val mComponentMap = scala.collection.mutable.Map.empty[Manifest[_],Component]
  var game:GameContext = null
  def contains (components :Seq[Manifest[_]]) = {
    var result = true
    if (components.isEmpty) result = false
    for (c <- components){
      mComponentMap get c match {
        case Some(x) =>
        case None => result = false
      }
    }
    result
  }

  /**
   * add component
   * @param c
   */
  def register (c:Component)= mComponentMap(Manifest.classType(c.getClass)) = c

  /**
   * initialize all components
   */
  def initialize(g:GameContext) = {
    game = g
    mComponentMap.foreach(_._2.initialize(this))
  }

  /**
   * finish all components
   */
  def finish() = {
    mComponentMap.foreach(_._2.finish(this))
  }


  def get[T](implicit componentType : Manifest[T]):T = {
    //Glyph.log("get call:"+componentType.runtimeClass.getSimpleName)
    mComponentMap get componentType match{
      case Some(x) => x.asInstanceOf[T]
    }
  }
  def mayBeGet[T](implicit componentType : Manifest[T]):Option[T] = {
    //Glyph.log("mayBeGet call:"+componentType.runtimeClass.getSimpleName)
    val res = mComponentMap get componentType
    res.asInstanceOf[Option[T]]
  }
}
