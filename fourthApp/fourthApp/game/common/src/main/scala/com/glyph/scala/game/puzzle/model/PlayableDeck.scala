package com.glyph.scala.game.puzzle.model

import cards.Card
import com.glyph.scala.lib.util.reactive.{EventSource, Var}
import collection.immutable.Queue

/**
 * @author glyph
 */

class PlayableDeck[T:Manifest](controller: T) {
  type PCard = Card[T]#PlayableCard
  val deck = Var(Queue.empty[Card[T]#PlayableCard])
  val hand = Var(Queue.empty[Card[T]#PlayableCard])
  val discarded = Var(Queue.empty[Card[T]#PlayableCard])
  val drawCardEvent = EventSource[Card[T]#PlayableCard]()
  val discardEvent = EventSource[Card[T]#PlayableCard]()

  def addCard(card: PCard) {
    deck() = deck().enqueue(card)
  }

  def drawCard() {
    if (deck().isEmpty) {
      deck() ++= discarded()
      discarded() = Queue.empty
      //deck() = deck().enqueue(new Scanner)
    }
    val (drawn, d) = deck().dequeue
    deck() = d
    hand() = hand().enqueue(drawn)
    drawCardEvent.emit(drawn)
  }

  def discard(card: PCard) {
    if (hand().contains(card)) {
      hand() = hand().diff(card :: Nil)
      discarded() = discarded().enqueue(card)
      discardEvent.emit(card)
    }
  }
}
