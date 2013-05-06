package com.glyph.scala.lib.engine

/**
 * @author glyph
 */
trait Interface {
  var owner :Entity= null
  def onAttached(entity:Entity){
    owner = entity
  }
}
