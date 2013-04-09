package com.glyph.scala.system

import com.glyph.scala.lib.entity_component_system.EntityContainer

/**
 * Created with IntelliJ IDEA.
 * User: glyph
 * Date: 13/04/08
 * Time: 1:32
 * To change this template use File | Settings | File Templates.
 */
trait GameSystem {
  def update(delta: Float,container:EntityContainer){}
}
