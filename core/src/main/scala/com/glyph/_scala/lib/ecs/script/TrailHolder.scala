package com.glyph._scala.lib.ecs.script

import com.glyph._scala.lib.libgdx.gl.UVTrail
import com.glyph._scala.lib.ecs.Entity
import com.glyph._scala.lib.ecs.system.TrailRenderer
import com.badlogic.gdx.math.Vector2
import com.glyph.ClassMacro._
/**
 * @author glyph
 */
class TrailHolder extends Script{
  var renderer:TrailRenderer = null
  val trail = new UVTrail(5)
  val tmp = new Vector2
  var transform:Transform = null

  override def initialize(self: Entity): Unit = {
    super.initialize(self)
    renderer = self.scene.getSystem[TrailRenderer]
    transform = self.getScript[Transform]
    renderer += trail
  }


  override def update(delta: Float): Unit = {
    super.update(delta)
    transform.matrix.getTranslation(tmp)
    trail.addTrail(tmp.x,tmp.y)
  }

  override def reset(): Unit = {
    super.reset()
    trail.reset()
    if(renderer != null) renderer -= trail
    renderer = null
    transform = null
  }
}