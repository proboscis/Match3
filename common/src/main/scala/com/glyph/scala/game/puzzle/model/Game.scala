package com.glyph.scala.game.puzzle.model

import com.glyph.scala.lib.util.reactive.RFile
import com.glyph.scala.game.puzzle.model.match_puzzle.Match3

/**
 * ゲームの目的：
 * パネルに潜む敵に殺されないようにパネルを消し続け、
 * レベル１００まで到達すること。
 * @author glyph
 */
class Game(fileSrc: String => RFile) {
  val player = new Player(fileSrc("json/player.json"))
  val dungeon = new Dungeon
  val puzzle = new Match3(() => {
    dungeon.getPanel(player.position())
  })
}