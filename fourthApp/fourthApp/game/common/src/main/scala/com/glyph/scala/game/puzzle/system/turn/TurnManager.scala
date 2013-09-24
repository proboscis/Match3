package com.glyph.scala.game.puzzle.system.turn

import collection.immutable.Queue
import com.glyph.scala.lib.util.reactive
import reactive._
import com.glyph.scala.game.puzzle.system.TurnProcessor
import com.glyph.scala.game.puzzle.model.Player
import com.glyph.scala.game.puzzle.controller.PuzzleGameController

/**
 *
 * @author glyph
 */
class TurnManager(player: Player, processors: Varying[Queue[TurnProcessor]], controller: PuzzleGameController) extends Reactor {
  private var previous = Queue.empty[TurnProcessor]
  val processorQueue = Var(Queue[TurnProcessor](player))

  def getProcessors = player +: processors()

  val cycleEnd = EventSource[Unit]()
  reactVar(processors) {
    queue =>
      val added = queue diff previous
      val removed = previous diff queue
      processorQueue() = processorQueue() diff removed
      processorQueue() ++= added
      previous = queue
  }

  def processStart() {
    if (processorQueue().isEmpty) {
      //initially this is filled.
      processorQueue() ++= getProcessors
      //processorQueue().enqueue=(getProcessors)// processorQueue().enqueue(getProcessors)
    }
    processNext()
  }

  // the processor must notify this manager that it has finished its job...
  //this is enough to handle this time...
  def processNext() {
    if (processorQueue().isEmpty) {
      cycleEnd.emit(Unit)
    } else {
      val (queued, queue) = processorQueue().dequeue
      processorQueue() = queue
      //println("next:" + queued)
      once(queued.turnFinished) {
        processNext()
      }
      queued.onTurnStart(controller)
    }
  }
}
