package com.glyph.scala.lib.util.updatable.task

import com.glyph.scala.lib.util.Logging

/**
 * @author glyph
 */
trait TimedTask extends Task with Logging with AutoFree{
  self =>
  protected var duration: Float = 0f
  protected var time = 0f
  protected var completed = false

  override def reset(){
    super.reset()
    duration = 0
    time = 0
    completed = false
  }
  override def update(delta: Float) {
    if (!completed) {
      time += delta
      completed = time >= duration
    }
  }

  def in(d:Float):this.type = {
    duration = d
    this
  }

  def for_(d: Float):this.type = {
    duration = d
    this
  }

  def isCompleted: Boolean = completed
}
