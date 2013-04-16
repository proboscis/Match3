package com.glyph.scala.lib.entity_component_system

/**
 * @author glyph
 */
class SystemManager(val game: GameContext) {
  val systemMap = collection.mutable.HashMap.empty[Manifest[_], GameSystem]

  def addSystem[T](s: GameSystem)(implicit typ: Manifest[T]) {
    systemMap(typ) = s
  }

  def removeSystem[T](s: GameSystem)(implicit typ: Manifest[T]) {
    systemMap(typ) = null
  }

  def getSystem[T](implicit typ: Manifest[T]): T = {
    systemMap(typ).asInstanceOf[T]
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

