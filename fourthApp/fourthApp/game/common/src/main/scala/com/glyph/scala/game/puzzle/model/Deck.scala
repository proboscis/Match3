package com.glyph.scala.game.puzzle.model

import cards.{Scanner, Card}
import com.glyph.scala.lib.util.reactive.{EventSource, Var}
import collection.immutable.Queue
/**
 * @author glyph
 */

class Deck {
  val deck = Var(Queue.empty[Card])
  val hand = Var(Queue.empty[Card])
  val discarded = Var(Queue.empty[Card])
  val drawCardEvent = EventSource[Card]()
  val discardEvent = EventSource[Card]()

  def addCard(card: Card) {
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

  def discard(card: Card) {
    if (hand().contains(card)) {
      hand() = hand().diff(card :: Nil)
      discarded() = discarded().enqueue(card)
      discardEvent.emit(card)
    }
  }
}
