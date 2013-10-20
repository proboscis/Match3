package com.glyph.scala.lib.util.updatable.task

/**
 * @author glyph
 */
trait TimedTask extends Task {
  self =>
  var duration: Float
  var time = 0f
  protected var completed = false

  override def update(delta: Float) {
    if (!completed) {
      time += delta
      completed = time >= duration
    }
  }

  def for_(d: Float):this.type = {
    duration = d
    this
  }

  def isCompleted: Boolean = completed
}
