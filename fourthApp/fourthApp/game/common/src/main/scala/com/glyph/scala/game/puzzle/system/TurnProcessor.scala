package com.glyph.scala.game.puzzle.system

import com.glyph.scala.lib.util.reactive.EventSource
import turn.TurnManager

/**
 * @author glyph
 */
trait TurnProcessor {
  val processFinish = EventSource[Unit]()
  /**
   * at least one must
   * @param manager TurnManager
   */
  def process(manager:TurnManager)
  def end(){
    processFinish.emit(Unit)
  }
}
