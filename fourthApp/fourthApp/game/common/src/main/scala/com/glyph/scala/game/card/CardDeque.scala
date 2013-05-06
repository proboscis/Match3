package com.glyph.scala.game.card

import collection.mutable
import com.glyph.scala.lib.util.observer.Observable

/**
 * @author glyph
 */
class CardDeque extends Observable[CardDeque]{
  import CardDeque._
  var deque = mutable.Queue.empty[Card]
  val hands = mutable.Queue.empty[Card]
  var discards = mutable.Queue.empty[Card]
  /**
   * init deque
   */
  (1 to 40).foreach{_=>deque.enqueue(new Card)}

  def drawCard(){
    val card = deque.dequeue()
    hands.enqueue(card)
    notifyObservers(DrawCard(card))
  }
}
object CardDeque{
  case class DrawCard(card:Card)
}
