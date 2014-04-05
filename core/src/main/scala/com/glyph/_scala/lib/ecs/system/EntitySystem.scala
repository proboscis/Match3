package com.glyph._scala.lib.ecs.system

import com.glyph._scala.lib.ecs.Scene

/**
 * @author glyph
 */
trait EntitySystem {
  def update(scene:Scene,delta:Float)
  def draw(scene:Scene)
}
