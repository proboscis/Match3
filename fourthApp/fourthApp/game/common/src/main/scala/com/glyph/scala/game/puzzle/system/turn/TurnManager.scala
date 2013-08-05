package com.glyph.scala.game.puzzle.system.turn

import collection.immutable.Queue
import com.glyph.scala.lib.util.reactive
import reactive._
import com.glyph.scala.game.puzzle.system.TurnProcessor

/**
 *
 * @author glyph
 */
class TurnManager(processors :Varying[Queue[TurnProcessor]]) extends Reactor {
  var previous = processors.current
  val processorQueue = Var(Queue.empty[TurnProcessor])
  reactVar(processors){
    queue=>
      processorQueue() = processorQueue() diff (previous diff queue)
      previous = queue
  }
  // the processor must notify this manager that it has finished its job...
  //this is enough to handle this time...
  def processNext() {
    if (processorQueue().isEmpty) {
      processorQueue() = processorQueue().enqueue(processors())
    }
    val (queued, queue) = processorQueue().dequeue
    processorQueue() = queue
    once(queued.processFinish) {
      processNext()
    }
    queued.process(this)
  }

}
