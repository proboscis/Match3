package com.glyph.test

import com.glyph.scala.game.puzzle.model.Game
import com.glyph.scala.game.puzzle.controller.PuzzleGameController
import com.glyph.scala.lib.util.reactive.RFile
import com.glyph.scala.game.puzzle.model.match_puzzle.Panel

/**
 * @author glyph
 */
object PuzzleTest {

  def main(args: Array[String]) {
    val game = new Game(str => new RFile("common/src/main/resources/" + str))
    val puzzle = game.puzzle
    val controller = new PuzzleGameController(game)
    controller.initialize()
    val first = puzzle.panels()
    puzzle.swap(0,0,1,1)
    puzzle.swap(0,0,1,1)
    val second = puzzle.panels()

    println(first==second)

  }
}
