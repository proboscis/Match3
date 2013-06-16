package com.glyph.scala.game.puzzle.model

/**
 * ゲームの目的：
 * パネルに潜む敵に殺されないようにパネルを消し続け、
 * レベル１００まで到達すること。
 * @author glyph
 */
class Game {
  val puzzle = new Puzzle
  val deck = new Deck
  def initialize(){
    (1 to 40) foreach{_=>
      deck.deck.push(new Card)
    }
    (1 to 5) foreach {
      _=>deck.drawCard()
    }
    puzzle.initPanel()
  }
  def drawCard(){
    deck.drawCard()
  }
}
