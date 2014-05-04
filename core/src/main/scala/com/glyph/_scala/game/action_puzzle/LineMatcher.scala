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
  def scanAll(puzzle: Puzzle, width: Int, height: Int, filter: (T, T) => Int, callback: IndexedSeq[T] => Unit) {
    //assert(filter != null)
    var i = 0
    while (i < width) {
      scanVertical2(puzzle, i, height, filter, callback)
      i += 1
    }
    i = 0
    while (i < height) {
      scanHorizontal2(puzzle, i, width, filter, callback)
      i += 1
    }
  }

  def scanHorizontal2(puzzle: Puzzle, y: Int, width: Int, filter: (T, T) => Int, callback: IndexedSeq[T] => Unit) {
    scanBuf.clear()
    var i = 0
    while (i < width) {
      val row = puzzle(i)
      if (y < row.size){
        val current = row(y)
        scanBuf += current
        var j = i + 1
        while (j < width && {
          val nextCol = puzzle(j)
          if (y < nextCol.size) {
            val next = nextCol(y)
            if (filter(current, next) != -1) {
              scanBuf += next
              j+=1
              true
            } else false
          } else false
        }) {}
        i = j
        callback(scanBuf)
      } else {
        i += 1
      }
      scanBuf.clear()
    }
  }

  def scanVertical2(puzzle: Puzzle, x: Int, height: Int, filter: (T, T) => Int, callback: IndexedSeq[T] => Unit) {
    scanBuf.clear()
    var i = 0
    val row = puzzle(x)
    val size = row.size
    while (i < height) {
      if (i < size) {
        val current = row(i)
        scanBuf += current
        var j = i + 1
        while (j < height && {
          if (j < size) {
            val next = row(j)
            if (filter(current, next) != -1) {
              scanBuf += next
              j += 1
              true
            } else false
          } else false
        }) {}
        callback(scanBuf)
        i = j
      } else {
        i += 1
      }
      scanBuf.clear()
    }
  }
}

class Line3Matcher[T](filter:(T,T)=>Int) extends LineMatcher[T] with Marker[T]{
  import ActionPuzzle._
  private final var currentDestination:PatternBuffer[T] = null
  private final var currentAllocator:()=>PanelBuffer[T] = null
  val callback = (panels:Panels[T])=>{
    if(panels.size >= 3)
      currentDestination += (currentAllocator() ++= panels)
  }:Unit



  def mark(puzzle:Puzzle, row: Int, col: Int, dst:PatternBuffer[T], allocator: () => PanelBuffer[T]): Unit = {
    currentDestination = dst
    currentAllocator = allocator
    scanAll(puzzle,row,col,filter,callback)
    currentDestination = null
    currentAllocator = null
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
