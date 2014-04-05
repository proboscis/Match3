package com.glyph._scala.lib.libgdx.particle

/**
 * @author glyph
 */
class PInvoker(var target:PEntity => Unit) extends PModifier{
  var invoked = false
  def this() = this(null)
  override def onUpdate(entity: PEntity, delta: Float): Unit = {
    super.onUpdate(entity, delta)
    if(!invoked){
      err("invoke")
      target(entity)
      invoked = true
      entity -= this
    }
  }

  override def reset(): Unit = {
    super.reset()
    invoked = false
    target = null
  }
  def setTarget(f:PEntity => Unit):this.type = {
    this.target = f
    this
  }
}
