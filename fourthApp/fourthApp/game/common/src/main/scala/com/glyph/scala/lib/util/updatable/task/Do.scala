package com.glyph.scala.lib.util.updatable.task

/**
 * @author glyph
 */
class Do(f: =>Unit) extends Task{
  private var invoked = false
  def isCompleted: Boolean = invoked

  override def update(delta: Float) {
    super.update(delta)
    if(!invoked){
      f
      invoked = true
    }
  }
}
object Do{
  def apply(f: =>Unit):Do = {
    new Do(f)
  }
}