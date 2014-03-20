package com.glyph._scala.lib.util.animation

//this class guarantees that there is no multiple in and out animations at the same time.
trait AnimationKeeper{
  case class AnimationSet()
  def queuePush(in:Animation,out:Animation,pause:Animation,resume:Animation)
  def queuePop()
}