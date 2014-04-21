package com.glyph._scala.game.action_puzzle

import scala.collection.mutable.ArrayBuffer
import com.glyph._scala.lib.util.pool.{Poolable, Pooling}
import com.glyph._scala.game.Glyphs
import com.glyph._scala.lib.util.Logging

/**
 * how should this callback when matched?
 * @author glyph
 */
class PatternMatcher[T](patterns:IndexedSeq[Array[(Int,Int)]])
  extends Matcher[ActionPuzzle[T]#AP]
  with Logging{
  //TODO consider boundary of the pattern and optimize matching.
  /**
   * callbacks with the same instance of patterns, and matched panel sequences
   */
  type Callback = (IndexedSeq[(Int,Int)],IndexedSeq[ActionPuzzle[T]#AP])=>Unit
  val callbacks = ArrayBuffer[Callback]()
  private val callbackBuf = ArrayBuffer[ActionPuzzle[T]#AP]()
  def patternMatch(puzzle: IndexedSeq[IndexedSeq[ActionPuzzle[T]#AP]], row: Int, col: Int): Unit = {
    //log("begin patten match")
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
          val it = PanelIterator(puzzle, row, col, pattern, x, y)
          if(it != null) {
            while(it.hasNext) callbackBuf += it.next
            it.freeToPool()
            val head = callbackBuf.head
            if(callbackBuf.forall(_.value == head.value)){
              var ci = 0
              val cSize = callbacks.size
              while(ci < cSize){
                callbacks(ci)(pattern,callbackBuf)
                ci += 1
              }
            }
            callbackBuf.clear()
          }
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
/*
class LineMatcher[T] extends Matcher[ActionPuzzle[T]#AP] {
  type Puzzle = IndexedSeq[IndexedSeq[T]]
  val scanBuf = ArrayBuffer[T]()

  def scanAll(puzzle: Puzzle, width: Int, height: Int, filter: (T, T) => Int, callback: Seq[T] => Unit) {
    var i = 0
    while (i < width) {
      scanVertical(puzzle, i, height, filter, callback)
      i += 1
    }
    i = 0
    while (i < height) {
      scanHorizontal(puzzle, i, width, filter, callback)
      i += 1
    }
  }

  def scanHorizontal(puzzle: Puzzle, y: Int, width: Int, filter: (T, T) => Int, callback: Seq[T] => Unit) {
    scanBuf.clear()
    var i = 0
    while (i < width) {
      val row = puzzle(i)
      val current = if (y < row.size) row(y) else null.asInstanceOf[T]
      scanBuf += current
      var j = i + 1
      while (j < width && {
        val nextRow = puzzle(j)
        val next = if (y < nextRow.size) nextRow(y) else null.asInstanceOf[T]
        if (filter(current, next) != -1) {
          scanBuf += next
          j += 1
          true
        } else false
      }) {}
      callback(scanBuf)
      scanBuf.clear()
      i = j
    }
  }

  def scanVertical(puzzle: Puzzle, x: Int, height: Int, filter: (T, T) => Int, callback: Seq[T] => Unit) {
    scanBuf.clear()
    var i = 0
    val row = puzzle(x)
    val size = row.size
    while (i < height) {
      val current = if (i < size) row(i) else null.asInstanceOf[T]
      scanBuf += current
      var j = i + 1
      while (j < height && {
        val next = if (j < size) row(j) else null.asInstanceOf[T]
        if (filter(current, next) != -1) {
          scanBuf += next
          j += 1
          true
        } else false
      }) {}
      callback(scanBuf)
      scanBuf.clear()
      i = j
    }
  }
}
*/