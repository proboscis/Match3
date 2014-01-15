package com.glyph.scala.lib.util.updatable.task

/**
 * @author glyph
 */
class ThreadTask(f:()=>Unit) extends Task{
  var started = false
  val thread = new Thread(new Runnable{
    def run(){f()}
  })
  def isCompleted: Boolean = started && !thread.isAlive

  override def onStart(): Unit = {
    super.onStart()
    started = true
    thread.start()
  }
}
