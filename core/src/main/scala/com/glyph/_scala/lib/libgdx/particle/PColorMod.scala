package com.glyph._scala.lib.libgdx.particle

import com.badlogic.gdx.graphics.Color

/**
 * @author glyph
 */
object PColorMod{
  val linear = (f:Float)=>f
}

class PColorMod(var interpolation:Float=>Float,var duration:Float,var loop:Boolean) extends PModifier{
  val start = new Color
  val end = new Color
  val tmp = new Color
  var elapsedTime = -1f
  def this() = this(PColorMod.linear,1f,false)

  override def onUpdate(entity: PEntity, delta: Float): Unit = {
    super.onUpdate(entity, delta)
    if(elapsedTime < 0) elapsedTime = 0
    elapsedTime  += delta
    if(loop && elapsedTime >= duration) elapsedTime -= duration
    entity.color.set(tmp.set(start).lerp(end,interpolation(elapsedTime/duration)))
  }
  override def reset(): Unit = {
    super.reset()
    start.set(1,1,1,1)
    end.set(1,1,1,1)
    interpolation = null
    duration = 1f
    loop = false
    elapsedTime = -1f// this is a flag of initialization
  }
}