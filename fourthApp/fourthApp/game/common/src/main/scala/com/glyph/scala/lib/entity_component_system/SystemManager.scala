package com.glyph.scala.lib.entity_component_system

import com.glyph.scala.Glyph

/**
 * @author glyph
 */
class SystemManager(val game: GameContext) {
  val systemMap = collection.mutable.HashMap.empty[Manifest[_], GameSystem]

  def addSystem[T<:GameSystem](s: T)(implicit typ: Manifest[T]) {
    Glyph.log("SystemManager","addSystem:"+typ)
    systemMap(typ) = s
  }

  def removeSystem[T<:GameSystem](s: T)(implicit typ: Manifest[T]) {
    systemMap(typ) = null
  }

  def getSystem[T<:GameSystem](implicit typ: Manifest[T]): T = {
    systemMap(typ).asInstanceOf[T]
  }

  def safeGetSystem[T<:GameSystem](implicit typ:Manifest[T]):Option[T] = {
    return systemMap get typ match{
      case Some(x) => Option(x.asInstanceOf[T])
      case None=> None
    }
  }

  def update(delta: Float) {
    systemMap.foreach {
      _._2.update(delta)
    }
  }

  def dispose() {
    systemMap.foreach {
      _._2.dispose()
    }
    systemMap.clear()
  }
}

