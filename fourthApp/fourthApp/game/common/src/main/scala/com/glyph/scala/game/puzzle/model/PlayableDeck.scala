package com.glyph.scala.game.puzzle.model

import cards.{Charge, Card}
import com.glyph.scala.lib.util.reactive.{EventSource, Var}
import collection.immutable.Queue
import com.glyph.scala.game.puzzle.view.PlayableCardDescription
import com.glyph.scala.game.puzzle.controller.PuzzleGameController

/**
 * @author glyph
 */

class PlayableDeck(controller:PuzzleGameController) {
  val deck = Var(Queue.empty[Card#PlayableCard])
  val hand = Var(Queue.empty[Card#PlayableCard])
  val discarded = Var(Queue.empty[Card#PlayableCard])
  val drawCardEvent = EventSource[Card#PlayableCard]()
  val discardEvent = EventSource[Card#PlayableCard]()

  def addCard(card: Card#PlayableCard) {
    deck()= deck().enqueue(card)
  }

  def drawCard() {
    if (deck().isEmpty) {
      deck() ++= discarded()
      discarded() = Queue.empty
      //deck() = deck().enqueue(new Scanner)
    }
    val (drawn, d) = deck().dequeue
    deck ()= d
    hand() = hand().enqueue(drawn)
    drawCardEvent.emit(drawn)
  }

  def discard(card: Card#PlayableCard) {
    if (hand().contains(card)) {
      hand() = hand().diff(card :: Nil)
      discarded() = discarded().enqueue(card)
      discardEvent.emit(card)
    }
  }
}
