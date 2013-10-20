package com.glyph.scala.game.puzzle.model.chain_puzzle

/**
 * @author glyph
 */
trait ChainPanel {
  /**
   *
   * @param puzzle puzzle game instance
   * @param animation exploding animation
   */
  def apply(puzzle:ChainPuzzle,animation:Option[Animation] = None)
}
