package com.glyph._scala.lib.ecs.script

import com.glyph._scala.lib.ecs.component.ElapsedTime
import com.glyph._scala.lib.ecs.Entity

/**
 * @author glyph
 */
class Expire extends Script{
  var duration = 0f
  private var et :ElapsedTime = null
  override def initialize(self: Entity): Unit = {
    super.initialize(self)
    et = self.component[ElapsedTime]
  }

  override def update(delta: Float): Unit = {
    super.update(delta)
    if(et.elapsedTime >=duration){
      entity.remove()//TODO this causes iterator cannot be nested bug... why?
    }
  }

  override def reset(): Unit = {
    super.reset()
    duration = 0f
    et = null
  }
}
