package com.glyph.scala.lib.util.updatable.task

/**
 * @author glyph
 */
class Wait(w:(Wait)=> Unit) extends Task{
  var invoked = false
  var woken = false
  def isCompleted: Boolean = invoked && woken
  override def update(delta: Float) {
    super.update(delta)
    if (!invoked){
      w(this)
      invoked = true
    }
  }
  def wake(){
    woken = true
  }
}
object Wait{
  def apply(w:(Wait)=>Unit):Wait={
    new Wait(w)
  }
}
