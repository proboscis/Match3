package com.glyph.scala.game.puzzle.system

import com.glyph.scala.lib.util.reactive.{Var, Varying, EventSource}
import turn.TurnManager
import com.glyph.scala.game.puzzle.controller.PuzzleGameController
import com.glyph.scala.game.puzzle.view.PuzzleGameView

/**
 * @author glyph
 */
trait TurnProcessor {
  val turnStarted = EventSource[Unit]()
  val turnFinished = EventSource[Unit]()
  /**
   * you cannot call end() inside this method or it will cause a stack over flow
   */
  def onTurnStart(controller:PuzzleGameController){
    turnStarted.emit()
  }

  /**
   * call this method to finish processing the turn.
   */
  def turnEnd(){
    turnFinished.emit(Unit)
  }
}