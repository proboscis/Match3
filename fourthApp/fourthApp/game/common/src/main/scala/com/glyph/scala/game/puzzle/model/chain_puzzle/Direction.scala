package com.glyph.scala.game.puzzle.model.chain_puzzle


/**
 * @author glyph
 */
class Direction(val direction: Int) extends ChainPanel {
  import Direction._
  def apply(puzzle: ChainPuzzle, animationOp: Option[Animation]) {
    animationOp.getOrElse(Animation.DUMMY){
      val ps = puzzle.panels()
      directionToPanels(direction,puzzle,this) foreach {
        _(puzzle, animationOp)
      }
    }
  }
}

object Direction {
  val NONE = 0
  val UP = 0x000000001
  val RIGHT = 0x00000010
  val LEFT = 0x00000100
  val DOWN = 0x00001000
  val RIGHT_UP = 0x00010000
  val LEFT_UP = 0x00100000
  val RIGHT_DOWN = 0x01000000
  val LEFT_DOWN = 0x10000000
  val dirToOffset = Map(
    NONE ->(0, 0),
    UP ->(0, 1),
    RIGHT ->(1, 0),
    LEFT ->(-1, 0),
    DOWN ->(0, -1),
    RIGHT_UP ->(1, 1),
    LEFT_UP ->(-1, 1),
    RIGHT_DOWN ->(1, -1),
    LEFT_DOWN ->(-1, -1))
  val directions = UP :: RIGHT :: LEFT :: DOWN ::
    RIGHT_UP :: LEFT_UP :: RIGHT_DOWN :: LEFT_DOWN :: Nil
  def directionToPanels(dir: Int,puzzle:ChainPuzzle,panel:ChainPanel): Seq[ChainPanel] = {
    val panels = puzzle.panels()
    puzzle.indexOf(panel) map {
      case (x, y) =>
        (dirToOffset.collect {
          case (k, (ox, oy)) if (k & dir) > 0 => (x + ox, y + oy)
        } map {
          case (x, y) => panels(x)(y)
        }).toSeq
    } getOrElse Nil
  }
}
