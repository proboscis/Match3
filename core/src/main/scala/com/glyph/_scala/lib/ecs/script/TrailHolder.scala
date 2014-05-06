package com.glyph._scala.lib.ecs.script

import com.glyph._scala.lib.libgdx.gl.UVTrail
import com.glyph._scala.lib.ecs.Entity
import com.glyph._scala.lib.ecs.system.TrailRenderer
import com.badlogic.gdx.math.Vector2
import com.glyph.ClassMacro._
import com.glyph._scala.lib.ecs.component.{Tint, Transform}

/**
 * @author glyph
 */
class TrailHolder extends Script{
  var renderer:TrailRenderer = null
  val trail = new UVTrail(15)
  val tmp = new Vector2
  var transform:Transform = null
  var tint:Tint = null

  override def initialize(self: Entity): Unit = {
    super.initialize(self)
    renderer = self.scene.getSystem[TrailRenderer]
    transform = self.component[Transform]
    tint = self.component[Tint]
    renderer += trail
  }

  override def update(delta: Float): Unit = {
    super.update(delta)
    transform.matrix.getTranslation(tmp)
    trail.addTrail(tmp.x,tmp.y)
    trail.color.set(tint.color)
  }

  override def reset(): Unit = {
    super.reset()
    trail.reset()
    if(renderer != null) renderer -= trail
    renderer = null
    transform = null
    tint = null
  }
}