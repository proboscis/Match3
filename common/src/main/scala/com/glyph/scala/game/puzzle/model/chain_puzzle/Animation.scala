package com.glyph.scala.game.puzzle.model.chain_puzzle

/**
 * @author glyph
 */
trait Animation {
  /**
   * you must provide a new animation for each invocation
   * @param after block
   */
  def apply(after: =>Unit)
}
object Animation{
  /**
   * this is a dummy that animates nothing and process next
   */
  val DUMMY = new Animation{
    def apply(after: => Unit) {after}
  }
}
