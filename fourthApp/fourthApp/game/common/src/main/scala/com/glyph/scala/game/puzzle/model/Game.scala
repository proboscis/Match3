package com.glyph.scala.game.puzzle.model

import com.glyph.scala.lib.util.observer.reactive.Var

/**
 * ゲームの目的：
 * パネルに潜む敵に殺されないようにパネルを消し続け、
 * レベル１００まで到達すること。
 * @author glyph
 */
class Game {
  val player = new Player
  val puzzle = new Puzzle
  val deck = new Deck
  val action = Var(0) // action point
  val state = Var(Game.PLAYING)
  def initialize(){
    (1 to 40) foreach{_=>
      deck.deck.push(new Card)
    }
    (1 to 5) foreach {
      _=>deck.drawCard()
    }
    puzzle.fill(puzzle.createFilling)
  }
  def drawCard(){
    deck.drawCard()
  }
}
object Game extends Enumeration{
  val PLAYING,GAME_OVER = Value
}
