package com.glyph._scala.game.action_puzzle

import scala.collection.mutable.ArrayBuffer
import com.badlogic.gdx.math.MathUtils
import com.glyph.hello


/**
 * @author proboscis
 */
class LineMatcher[T] {
  type Puzzle = IndexedSeq[IndexedSeq[T]]
  val scanBuf = ArrayBuffer[T]()
  /**
   * @param puzzle
   * @param width
   * @param height
   * @param filter filter must return matching group id or -1 if it's not matching
   * @param callback
   */
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
          j+=1
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
object LineMatcher {
  val NO_MATCH = -1
}
object Main {
  def main(args: Array[String]) {
    import GMatch3._
    val puzzle = ArrayBuffer(1 to 6 map (_=>ArrayBuffer[Int]()) :_*)
    val matcher = new LineMatcher[Int]
    val filling = puzzle.createFillingPuzzle(()=>MathUtils.random(6),6)
    println(filling.text)
    matcher.scanAll(filling, 6, 6, (a, b) => if(a == b) a else -1,seq=>if(seq.size>1)println(seq))
  }
}
