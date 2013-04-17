package com.glyph.scala.lib.entity_component_system

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

  def has[T<:Component](implicit componentType:Manifest[T]):Boolean={
    mComponentMap.contains(componentType)
  }

  /**
   * use this when you need performance, <br>
   * and completely sure that this entity have such component
   * @param componentType
   * @tparam T
   * @return
   */
  def directGet[T<:Component](implicit componentType : Manifest[T]):T = {
    mComponentMap(componentType).asInstanceOf[T]
  }

  def get[T<:Component](implicit componentType : Manifest[T]):Option[T] = {
    mComponentMap.get(componentType).asInstanceOf[Option[T]]
  }
}
