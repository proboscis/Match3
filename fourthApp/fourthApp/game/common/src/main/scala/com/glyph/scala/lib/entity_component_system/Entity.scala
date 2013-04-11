package com.glyph.scala.lib.entity_component_system

import com.badlogic.gdx.Gdx
import com.glyph.scala.Glyph

class Entity {
  val mComponentMap = scala.collection.mutable.Map.empty[Manifest[_],Component]

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
  def initialize() = mComponentMap.foreach(_._2.initialize(this))
  def get[T](implicit componentType : Manifest[T]):T = {
    Glyph.log("get call")
    mComponentMap get componentType match{
      case Some(x) => x.asInstanceOf[T]
    }
  }
  def mayBeGet[T](implicit componentType : Manifest[T]):Option[T] = {
    Glyph.log("maybe get call")
    val res = mComponentMap get componentType
    res.asInstanceOf[Option[T]]
  }
}
