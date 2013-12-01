package com.glyph.scala.lib.libgdx.actor.action

import scalaz._
import Scalaz._
import com.glyph.scala.lib.libgdx.actor.Tasking
import com.glyph.scala.lib.util.updatable.task.FunctionTask
import com.glyph.scala.lib.util.animator.{AnimatedFloat2, Swinger}
import com.glyph.scala.lib.util.Logging

/**
 * @author glyph
 */
trait Shivering extends Tasking with Logging{
  val shiver = new FunctionTask()
  private var started = false
  def startShivering[T:AnimatedFloat2](tgt:T) {
    if (!started) {
      log("start shivering")
      shiver.setUpdater(Swinger.update(100, getX, getY)(tgt))
      add(shiver)
      started = true
    }
  }
  def stopShivering() {
    if(started){
      if(!shiver.isCompleted)shiver.finish()
      started = false
      log("stop shivering")
    }
  }
}
