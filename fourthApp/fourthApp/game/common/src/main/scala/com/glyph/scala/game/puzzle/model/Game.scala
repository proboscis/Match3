package com.glyph.scala.game.puzzle.model

import com.glyph.scala.lib.util.reactive.{RFile, Var, Reactor}
import com.badlogic.gdx.math.MathUtils
import Element.{Water, Thunder, Fire}
import monsters.{Monster, Slime}
import com.glyph.scala.game.puzzle.model.match_puzzle.{Move, Life, Match3}
import com.glyph.scala.game.puzzle.system.turn.TurnManager
import collection.immutable.Queue
import com.glyph.scala.game.puzzle.system.TurnProcessor
import com.glyph.scala.lib.libgdx.reactive.GdxFile

/**
 * ゲームの目的：
 * パネルに潜む敵に殺されないようにパネルを消し続け、
 * レベル１００まで到達すること。
 * @author glyph
 */
class Game(fileSrc:String=>RFile){
  val player = new Player(fileSrc("json/player.json"))
  val deck = new Deck
  val dungeon = new Dungeon
  /*val puzzle = new Match3(
    () => MathUtils.random(6) match {
      case 0 => new Fire
      case 1 => new Water
      case 2 => new Thunder
      case 3|4|5 => new Slime
      case 6 => new Life
      case 7 => new Move
    }
  )*/
  val puzzle = new Match3(()=>{
    dungeon.getPanel(player.position())
  })
}