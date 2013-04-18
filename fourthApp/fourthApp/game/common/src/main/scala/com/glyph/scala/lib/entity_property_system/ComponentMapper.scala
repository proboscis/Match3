package com.glyph.scala.lib.entity_property_system

import com.glyph.libgdx.util.ArrayBag

/**
 * @author glyph
 */
class ComponentMapper[T<:Component](private val components:ArrayBag[T]){
  def get(e:Entity):T={
    components.get(e.index)
  }
}
