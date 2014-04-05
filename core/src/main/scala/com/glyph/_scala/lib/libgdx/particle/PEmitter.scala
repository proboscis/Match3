package com.glyph._scala.lib.libgdx.particle

import com.badlogic.gdx.math.{Vector2, MathUtils}

/**
 * @author glyph
 */
class PEmitter(var generator:()=>PEntity) extends PModifier{
  // lots of parameters to apply!
  var timer = 0f
  var interval = 1f
  var elapsedTime = -1f
  override def onUpdate(entity: PEntity, delta: Float): Unit = {
    super.onUpdate(entity, delta)
    timer += delta
    while(timer >= interval){
      timer -= interval
      val newEntity = generator()
      entity += newEntity
    }
  }
  override def reset(): Unit = {
    super.reset()
    generator = null
    timer = 0f
    interval = 1f
  }
}

