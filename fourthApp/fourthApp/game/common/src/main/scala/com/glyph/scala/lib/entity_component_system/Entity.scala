package com.glyph.scala.lib.entity_component_system

import com.glyph.scala.Glyph

/**
 * Created with IntelliJ IDEA.
 * User: glyph
 * Date: 13/04/02
 * Time: 15:22
 * To change this template use File | Settings | File Templates.
 */
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
    mComponentMap get componentType match{
      case Some(x) => x.asInstanceOf[T]
    }
  }
}
