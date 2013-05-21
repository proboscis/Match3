package com.glyph.scala.lib.util.updatable.task

/**
 * @author glyph
 */
trait TimedTask extends Task{
  val duration:Float
  var time = 0f
  protected var completed = false

  override def update(delta: Float) {
    if (!completed){
      time += delta
      completed = time >= duration
    }
  }

  def isCompleted: Boolean = completed
}
