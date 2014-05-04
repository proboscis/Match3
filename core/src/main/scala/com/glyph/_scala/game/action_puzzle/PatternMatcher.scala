package com.glyph._scala.game.action_puzzle

import scala.collection.mutable.ArrayBuffer
import com.glyph._scala.lib.util.pool.{Poolable, Pooling}
import com.glyph._scala.game.Glyphs
import com.glyph._scala.lib.util.Logging
import ActionPuzzle._
import com.glyph._scala.lib.puzzle.Match3

/**
 * how should this callback when matched?
 * @author glyph
 */
class PatternMatcher[T](patterns:IndexedSeq[Array[(Int,Int)]],filter:(T,T)=>Boolean)
  extends Logging
  with Marker[T]{
  val evaluatingBuffer = ArrayBuffer[T]()
  def evaluate(panels:IndexedSeq[T]):Boolean = panels.forall(p => filter(p,panels.head))
  /**
   * fills dst, return false if failed. the dst will be cleared if failed.
   * @param puzzle
   * @param row
   * @param col
   * @param pattern
   * @param dst
   * @return
   */
  def fillEvaluatingBuffer(puzzle:Puzzle[T],row:Int,col:Int,ox:Int,oy:Int,pattern:Array[(Int,Int)],dst:PanelBuffer[T]):Boolean = {
    //log("fill")
    var pi = 0
    val pSize = pattern.size
    var success = true
    while(pi < pSize && success){
      val pos = pattern(pi)
      val x = pos._1 + ox; val y = pos._2 + oy
      //log(x,y)
      if(0 <= x && x < puzzle.size && 0 <= y && y < puzzle(x).size) dst += puzzle(x)(y) else {
        success = false
        dst.clear()
      }
      pi += 1
    }
    success
  }
  def mark(puzzle:Puzzle[T], row: Int, col: Int, dst:PatternBuffer[T], allocator: () => PanelBuffer[T]): Unit = {
    //log("marking:")
    //log(puzzle.mkString("\n"))
    var x = 0
    while (x < col) {
      //log("for each column")
      var y = 0
      while (y < row) {
        //log("for each row")
        var pi = 0
        val pSize = patterns.size
        while(pi < pSize){
          val pattern = patterns(pi)
          if(fillEvaluatingBuffer(puzzle,row,col,x,y,pattern,evaluatingBuffer)){
            if(evaluate(evaluatingBuffer)){
              dst += (allocator() ++= evaluatingBuffer)
            }
          }
          evaluatingBuffer.clear()
          pi += 1
        }
        y += 1
      }
      x += 1//damn while loop!!!
    }
  }
}

import Glyphs._
import com.glyph._scala.lib.util.pool.GlobalPool._

/**
 * assumes that the pattern is all within the puzzle field
 * @tparam T
 */
class PanelIterator[T] extends Iterator[T] with Poolable {
  var offsetX = 0
  var offsetY = 0
  var puzzle: IndexedSeq[IndexedSeq[T]] = null
  var row = 0
  var col = 0
  var pattern: IndexedSeq[(Int, Int)] = null
  var index = 0
  var patternSize = 0

  def hasNext: Boolean = index < patternSize

  def next(): T = {
    val n = pattern(index)
    index += 1
    puzzle(n._1 + offsetX)(n._2 + offsetY)
  }
}

object PanelIterator {

  implicit object PoolingPanelIterator extends Pooling[PanelIterator[Any]] {
    def newInstance: PanelIterator[Any] = new PanelIterator[Any]

    def reset(tgt: PanelIterator[Any]): Unit = {
      tgt.puzzle = null
      tgt.row = 0
      tgt.col = 0
      tgt.pattern = null
      tgt.index = 0
      tgt.patternSize = 0
      tgt.offsetX = 0
      tgt.offsetY = 0
    }
  }

  def apply[T](puzzle: IndexedSeq[IndexedSeq[T]], row: Int, col: Int, pattern: IndexedSeq[(Int, Int)], x: Int, y: Int) = {
    if ( {
      //you have to consider the actual size of the puzzle buffer
      var all = true
      var i = 0
      val size = pattern.size
      while (i < size && all) {
        val p = pattern(i)
        val px = p._1 + x
        val py = p._2 + y
        if (0 <= px && px < col && 0 <= py && py < puzzle(px).size) {/**ok**/} else {
          all = false
        }
        i += 1
      }
      all //valid
    }) {
      val it = auto[PanelIterator[Any]].asInstanceOf[PanelIterator[T]]
      it.offsetX = x
      it.offsetY = y
      it.puzzle = puzzle
      it.row = row
      it.col = col
      it.pattern = pattern
      it.index = 0
      it.patternSize = pattern.size
      it
    } else null
  }
}