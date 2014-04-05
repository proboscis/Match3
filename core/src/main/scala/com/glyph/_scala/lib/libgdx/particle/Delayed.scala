package com.glyph._scala.lib.libgdx.particle

/**
 * adds a generated entity to the attached entity after specified time
 * @author glyph
 */
class Delayed private(var generator:()=>PEntity,var delay:Float) extends PModifier{
  var elapsedTime = -1f
  var generated = false
  def this() = this(null,0f)

  override def onUpdate(entity: PEntity, delta: Float): Unit = {
    super.onUpdate(entity, delta)
    if(elapsedTime < 0){// init et at the first frame
      elapsedTime = 0
    }
    elapsedTime += delta
    if(elapsedTime >= delay && !generated){
      entity += generator()
      generated = true
      entity -= this
    }
  }

  def setGenerator(generator:()=>PEntity):this.type = {
    this.generator = generator
    this
  }
  def setDelay(delay:Float):this.type = {
    this.delay = delay
    this
  }

  override def reset(): Unit = {
    super.reset()
    elapsedTime = -1f
    generated = false
  }
}
