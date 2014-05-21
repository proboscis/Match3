package com.glyph._scala.lib.ecs.script

import com.glyph._scala.lib.ecs.{Scene, Entity}
import com.glyph._scala.lib.ecs.component.{Transform, ElapsedTime}
import ElapsedTime._
import Transform._
import com.badlogic.gdx.math.Vector2

/**
 * @author glyph
 */
class ParticleEmitter extends Script{
  var emitter:Entity=>Entity = null
  var interval = 0f
  var elapsedTime = 0f
  def setup(emitter:Entity=>Entity,interval:Float){
    this.emitter = emitter
    this.interval = interval
  }
  override def initialize(self: Entity): Unit = {
    super.initialize(self)
    assert(interval != 0f && emitter != null)
    elapsedTime = 0f
  }


  override def update(delta: Float): Unit = {
    super.update(delta)
    elapsedTime += delta
    while(elapsedTime >= interval){
      elapsedTime -= interval
      entity.parent += emitter(entity)
    }
  }

  override def reset(): Unit = {
    super.reset()
    interval = 0f
    emitter = null
    elapsedTime = 0f
  }
}
