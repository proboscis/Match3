package com.glyph.scala.lib.util.updatable.task

/**
 * @author glyph
 */
class Do(var block: ()=>Unit) extends Task{
  private var invoked = false
  def isCompleted: Boolean = invoked

  override def update(delta: Float) {
    super.update(delta)
    if(!invoked){
      block()
      invoked = true
    }
  }

  override def reset(): Unit = {
    super.reset()
    invoked = false
    block = null
  }
  def setCallback(f:()=>Unit){
    block = f
  }
}
object Do{
  def apply(f: =>Unit):Do = {
    new Do(()=>f)
  }
}