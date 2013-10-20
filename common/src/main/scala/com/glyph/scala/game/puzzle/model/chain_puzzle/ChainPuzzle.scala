package com.glyph.scala.game.puzzle.model.chain_puzzle

import com.glyph.scala.lib.util.reactive.{Varying, Var}

/**
 * @author glyph
 */
class ChainPuzzle {
  val ROW = 6
  val COLUMN = 6
  val panels :Varying[Vector[Vector[ChainPanel]]]= Var(
    Vector(1 to ROW map {
      x => Vector((1 to COLUMN) map{
          y =>new Direction(Direction.NONE)
      }:_*)
    }:_*)
  )
  def indexOf(panel:ChainPanel):Option[(Int,Int)]={
    import scala.util.control.Exception._
    allCatch opt {
      val ps = panels()
      val row = ps.collect{
        case buf if buf.contains(panel) => buf
      }.head
      (ps.indexOf(row),row.indexOf(panel))
    }
  }
}
