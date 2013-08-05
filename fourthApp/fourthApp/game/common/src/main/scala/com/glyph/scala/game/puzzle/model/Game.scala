package com.glyph.scala.game.puzzle.model

import com.glyph.scala.lib.util.reactive.{Var, Reactor}
import com.glyph.scala.game.puzzle.system.turn.TurnManager
import collection.immutable.Queue
import com.badlogic.gdx.math.MathUtils
import Element.{Water, Thunder, Fire}
import monsters.Slime
import puzzle.{Panel, Puzzle}


/**
 * ゲームの目的：
 * パネルに潜む敵に殺されないようにパネルを消し続け、
 * レベル１００まで到達すること。
 * @author glyph
 */
class Game extends Reactor {
  val player = new Player
  val puzzle = new Puzzle(
    ()=>MathUtils.random(3)match{
      case 0 => new Fire
      case 1 => new Water
      case 2 => new Thunder
      case 3 => new Slime
    }
  )
  val deck = new Deck
  val action = Var(0)
  val dungeon = new Dungeon
}