package com.glyph.test

import com.glyph.scala.game.puzzle.model.match_puzzle.Match3
import com.glyph.scala.game.puzzle.model.Dungeon

/**
 * @author glyph
 */
object Match3Test {
  def main(args: Array[String]) {
    import Match3._
    val dungeon = new Dungeon
    val puzzle = new Match3(()=>dungeon.getPanel(1))
    puzzle.fill(puzzle.createFilling)
    val panels = puzzle.panels()
    Match3.scanAll(panels) foreach println
  }
}
