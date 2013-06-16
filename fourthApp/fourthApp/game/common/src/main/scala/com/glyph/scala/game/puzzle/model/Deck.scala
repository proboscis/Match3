package com.glyph.scala.game.puzzle.model

import com.glyph.scala.lib.util.callback.Callback1
import com.glyph.scala.lib.util.collection.list.DoubleLinkedQueue

/**
 * @author glyph
 */
class Deck {
  val deck = new DoubleLinkedQueue[Card]
  val hand = new DoubleLinkedQueue[Card]
  val discarded = new DoubleLinkedQueue[Card]
  val onDrawCard = new Callback1[Card]

  //TODO ゲームのロジックマネージャの作成
  def drawCard() {
    if (deck.isEmpty){
      deck.enqueue(new Card)
    }
    val drawn = deck.dequeue()
    hand.push(drawn)
    onDrawCard(drawn)
  }
}
