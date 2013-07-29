package com.glyph.scala.game.puzzle.model

import cards.{Scanner, Card}
import com.glyph.scala.lib.util.observer.reactive.{Block, Varying, Reactor, Var}

/**
 * ゲームの目的：
 * パネルに潜む敵に殺されないようにパネルを消し続け、
 * レベル１００まで到達すること。
 * @author glyph
 */
class Game extends Reactor{
  import Game._
  val player = new Player
  val puzzle = new Puzzle
  val deck = new Deck
  val action = Var(0) // action point
  val state = player.hp->{life =>if (life <= 0) GAME_OVER else PLAYING}
}
object Game extends Enumeration{
  val PLAYING,GAME_OVER = Value
}
