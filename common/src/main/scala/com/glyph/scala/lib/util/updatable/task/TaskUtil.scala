package com.glyph.scala.lib.util.updatable.task

/**
 * @author glyph
 */
object TaskUtil {
  def delay(sec:Float)(f: =>Unit):Task={
    new TimedTask with CompleteHook{
      var duration: Float = sec
      onComplete(f)
    }
  }
}