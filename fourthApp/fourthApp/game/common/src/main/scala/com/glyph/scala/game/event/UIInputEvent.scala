package com.glyph.scala.game.event

import com.glyph.scala.lib.event.DebugEvent

/**
 * @author glyph
 */
@DebugEvent
class UIInputEvent(val typ:Int)
object UIInputEvent{
  val RIGHT_BUTTON = 0
  val LEFT_BUTTON = 1
  val EXEC_BUTTON = 2
}
