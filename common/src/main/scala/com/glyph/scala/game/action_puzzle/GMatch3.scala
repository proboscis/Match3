package com.glyph.scala.game.action_puzzle

import scala.annotation.tailrec
import scala.collection.mutable
import com.glyph.hello
import scalaz._
import Scalaz._
import scala.reflect.ClassTag
import scala.collection.mutable.ArrayBuffer

/**
 * @author glyph
 */
//@hello
object GMatch3 {
  //TODO make scanning faster!
  import scala.util.control.Exception._
  trait Panel {
    def matchTo(panel: Panel): Boolean
  }
  type Event[T <: Panel] = (T, Int, Int)
  type MatchedSet[T <: Panel] = Seq[Event[T]]
  type Events[T <: Panel] = Seq[Event[T]]
  type Puzzle[T <: Panel] = IndexedSeq[IndexedSeq[T]]

  //def initialize[T <: Panel](size: Int): Puzzle[T] = Vector(0 until size map (_ => Vector()): _*)
  //def initialize[T <: Panel :ClassTag](size: Int): Puzzle[T] = ArrayBuffer((0 until size).toList.as(ArrayBuffer()):_*)
  /*def initialize[T <: Panel :ClassTag](size: Int): Puzzle[T] = {
    def gen:IndexedSeq[T] = Array[T]()
    Array((0 until size) map (_=>gen):_*)
  }*/
  trait IndexedSeqGen{
    def convert[T](seq:Seq[T]):IndexedSeq[T]
  }
  def initialize[T<:Panel](width:Int)(c:IndexedSeqGen):Puzzle[T] = c.convert((0 until width).toList.as(c.convert(Nil)))

  //includedはフラグ表をつかって高速化する必要がある
  def included[T <: Panel](sets: MatchedSet[T], target: MatchedSet[T]): Boolean = target forall sets.contains


  def toIndexMap[T](puzzle:IndexedSeq[IndexedSeq[T]]): T Map (Int,Int) = puzzle.zipWithIndex.flatMap{
    case (row,x) => row.zipWithIndex.map{
      case (p,y) => p->(x,y)
    }
  }.toMap
  def toContainsMap[T](puzzle:IndexedSeq[IndexedSeq[T]]): T  Map Boolean = puzzle.flatMap{
    row => row.map{
      p => p->true
    }
  }.toMap.withDefaultValue(false)

  def scanIndexedWithException[T <: Panel](puzzle: Puzzle[T])(x: Int)(y: Int)(exception: T => Boolean)(right: Boolean): MatchedSet[T] = {
    val W = puzzle.size
    var matches = (puzzle(x)(y), x, y) :: Nil
    if (x < W) {
      val H = puzzle(x).size
      var current = puzzle(x)(y)
      if (right) {
        //right direction
        var nx = x + 1
        while (nx < W && y < puzzle(nx).size && current.matchTo(puzzle(nx)(y)) && !exception(current)) {
          matches ::=(puzzle(nx)(y), nx, y)
          current = puzzle(nx)(y)
          nx += 1
        }
      } else {
        //left direction
        var ny = y + 1
        while (ny < H && current.matchTo(puzzle(x)(ny)) && !exception(current)) {
          matches ::=(puzzle(x)(ny), x, ny)
          current = puzzle(x)(ny)
          ny += 1
        }
      }
    }
    //println(matches)
    matches
  }

  def segment[T](line: List[T])(divider: T => Boolean): List[List[T]] = {
    //@tailrec
    def rec(seq: List[T], processing: List[T]): List[List[T]] = seq match {
      case Nil => processing::Nil
      case head :: tail => {
        if (divider(head)) {
          rec(tail, head :: processing)
        }else{
          processing :: rec(tail, head :: Nil)
        }
      }
    }
    rec(line, Nil)
  }
  def segment2[T](line:List[T])(filter:(T,T)=>Boolean):List[List[T]] = {
    @tailrec
    def rec(seq:List[T],buffer:List[T],result:List[List[T]]):List[List[T]] = seq match{
      case Nil => buffer::result
      case head::Nil => (head::buffer)::result
      case first::second::tail => if (filter(first,second)){
        rec(second::tail,first::buffer,result)
      } else{
        rec(second::tail,Nil,(first::buffer)::result)
      }
    }
    rec(line,Nil,Nil)
  }
  def verticalLine[T](puzzle:IndexedSeq[IndexedSeq[T]])(x:Int)(y:Int)(height:Int):List[T] = {
    val W = puzzle.size
    var result = List.empty[T]
    if(x < W){
      val row = puzzle(x)
      val H = row.size
      var ny = y
      while(ny < height){
        if (ny < H){
          result ::= row(ny)
        }else{
          result ::= null.asInstanceOf[T]
        }
        ny += 1
      }
    }else{
      var ny = y
      while (ny < height){
        result ::= null.asInstanceOf[T]
        ny += 1
      }
    }
    result
  }
  def horizontalLine[T](puzzle:IndexedSeq[IndexedSeq[T]])(x:Int)(y:Int)(width:Int):List[T] ={
    val W = puzzle.size
    var result = List.empty[T]
    var nx = x
    while(nx < width){
      if(nx < W){
        val row = puzzle(nx)
        val H = row.size
        if( y < H){
          result ::= row(y)
        }else{
          result::= null.asInstanceOf[T]
        }
      }else{
        result ::= null.asInstanceOf[T]
      }
      nx += 1
    }
    result
  }

  def allLine[T](puzzle:IndexedSeq[IndexedSeq[T]])(width:Int)(height:Int):List[List[T]] = {
    var result:List[List[T]]= Nil
    var x = 0
    while ( x < width){
      result ::= verticalLine(puzzle)(x)(0)(height)
      x+=1
    }
    var y = 0

    while (y < height){
      result ::= horizontalLine(puzzle)(0)(y)(width)
      y += 1
    }
    result
  }

  def scanAll[T](puzzle:IndexedSeq[IndexedSeq[T]])(width:Int)(height:Int)(filter:(T,T)=>Boolean):Seq[Seq[T]] = allLine(puzzle)(width)(height) flatMap (segment2(_)(filter))

  def scanBy[T <: Panel](puzzle: Puzzle[T])(scanner: Int => Int => Boolean => MatchedSet[T]): Seq[MatchedSet[T]] = {
    val result = mutable.ArrayBuffer[MatchedSet[T]]()
    var x = 0
    val width = puzzle.size
    while (x < width) {
      var y = 0
      val height = puzzle(x).size
      while (y < height) {
        var vertical = false
        var flipCount = 0
        while (flipCount < 2) {
          val set = scanner(x)(y)(vertical)
          var i = 0
          val l = result.length
          var noNeedToAdd = false
          while (i < l) {
            //全てのマッチセットと重複がないかチェックしてしまっているため遅い
            val current = result(i)
            val inc = included(result(i), set)
            noNeedToAdd |= inc
            val needReplace = current.size < set.size && inc
            if (needReplace) result(i) = set
            i += 1
          }
          if (!noNeedToAdd && set != Nil) {
            result += set
          }
          vertical = !vertical
          flipCount += 1
        }
        y += 1
      }
      x += 1
    }
    result
  }

  def createFilling[T <: Panel](puzzle: Puzzle[T])(seed: () => T, col: Int): Events[T] = for (x <- 0 until puzzle.size; y <- puzzle(x).size until col) yield (seed(), x, y)

  def createFillingPuzzle[T <: Panel](puzzle: Puzzle[T])(seed: () => T, col: Int): Puzzle[T] = for (x <- 0 until puzzle.size) yield for (y <- puzzle(x).size until col) yield seed()

  def scanAllWithException[T <: Panel](puzzle: Puzzle[T])(matchLength: Int)(exception: T => Boolean) = scanBy(puzzle)(x => y => right => scanIndexedWithException(puzzle)(x)(y)(exception)(right)).filter {
    _.length >= matchLength
  }

  //@hello
  implicit class PuzzleImpl[T <: Panel](val puzzle: Puzzle[T]) extends AnyVal {
    def text: String = puzzle.map {
      col => col.map {
        _.toString
      }.fold("") {
        _ + "," + _
      }
    }.fold("") {
      _ + "\n" + _
    }

    def toIndexMap = GMatch3.toIndexMap(puzzle)

    def scanWithException = GMatch3.scanIndexedWithException(puzzle) _

    //TODO scannerを使うと動かしたときに消せるパネルを表示できるようにしたいね
    def scan(x: Int)(y: Int)(right: Boolean): MatchedSet[T] = {
      val W = puzzle.size
      val H = puzzle.head.size
      val panel = puzzle(x)(y)
      var matching = (puzzle(x)(y), x, y) :: Nil
      //TODO check nonempty
      if (right) {
        var nx = x + 1
        while (nx < W && y < puzzle(nx).size && panel.matchTo(puzzle(nx)(y))) {
          matching ::=(puzzle(nx)(y), nx, y)
          nx += 1
        }
      } else {
        var ny = y + 1
        val size = puzzle(x).size
        while (ny < H && ny < size && panel.matchTo(puzzle(x)(ny))) {
          matching ::=(puzzle(x)(ny), x, ny)
          ny += 1
        }
      }
      if (matching.size >= 3) matching else Nil
    }

    def createFilling = GMatch3.createFilling(puzzle) _

    def createFillingPuzzle = GMatch3.createFillingPuzzle(puzzle) _

    def fill(filling: Events[T]): Puzzle[T] = filling.foldLeft(puzzle) {
      case (p, (panel, x, _)) => p.updated(x, p(x) :+ panel)
    }

    def removePanels(panels: Seq[T]): Puzzle[T] = puzzle map (_ filterNot panels.contains)

    /**
     * @param panels to be removed
     * @return (leftPanels, floatingPanels)
     */
    def remove(panels: Seq[T]): (Puzzle[T], Puzzle[T]) = {
      val f = (p: Panel) => panels.contains(p)
      puzzle.unzip {
        col => col.span(!f(_)) match {
          case (left, float) => (left, float filterNot f)
        }
      }
    }

    def createNoMatchFilling(seed: () => T, col: Int): Events[T] = {
      val contains = puzzle.flatten.contains _
      @tailrec
      def fillWithStatics(added: Puzzle[T]): Puzzle[T] =
        added.createFilling(seed, col) match {
          case filling => added.fill(filling) match {
            case filled => filled.scanAll match {
              case matches if matches.flatten.map(_._1).forall(contains) => filled
              case matches => fillWithStatics(added.removePanels(filling.filterNot(puzzle.flatten.contains).map {
                _._1
              }))
            }
          }
        }
      for {
        (col, x) <- fillWithStatics(puzzle).zipWithIndex
        (panel, y) <- col.zipWithIndex if !puzzle(x).contains(panel)
      } yield (panel, x, y)
    }

    private def fillWithoutMatches(seed: () => T, size: Int): Puzzle[T] = {
      @tailrec
      def recFWM(p: Puzzle[T]): Puzzle[T] = {
        p.scanAll match {
          case Seq() => p
          case matches => recFWM(removePanels(matches.flatten.distinct.map {
            case (pp, x, y) => pp
          }))
        }
      }
      recFWM(puzzle)
    }

    def scanBy = GMatch3.scanBy(puzzle) _

    def scanAllWithException(matchLength: Int)(exception: T => Boolean) = scanBy(x => y => right => scanWithException(x)(y)(exception)(right)).filter {
      _.size >= matchLength
    }

    def scanAll = scanBy(scan)

    /**
     * @param panel searched with _.contains
     * @return index of given panel in a puzzle
     */
    def indexOfPanel(panel: T): Either[Throwable, (Int, Int)] = {
      //println(puzzle.flatten.contains(panel))
      allCatch either {
        val row = puzzle.filter {
          _.contains(panel)
        }.head
        (puzzle.indexOf(row), row.indexOf(panel))
      }
    }

    def indexOfPanelOpt(panel: T): Option[(Int, Int)] = allCatch opt {
      val row = puzzle.filter {
        _.contains(panel)
      }.head
      (puzzle.indexOf(row), row.indexOf(panel))
    }

    def indexOfPanelUnhandled(panel: Panel): (Int, Int) = {
      val row = puzzle.filter {
        _.contains(panel)
      }.head
      (puzzle.indexOf(row), row.indexOf(panel))
    }

    /**
     * swaps the give indices of puzzle, and returns new swapped puzzle
     * @return
     */
    def swap(ax: Int, ay: Int, bx: Int, by: Int): Puzzle[T] = {
      val a = puzzle(ax)(ay)
      val b = puzzle(bx)(by)
      val puzzle1 = puzzle.updated(ax, puzzle(ax).updated(ay, b))
      val puzzle2 = puzzle1.updated(bx, puzzle1(bx).updated(by, a))
      puzzle2
    }

    def append(p: Puzzle[T]): Puzzle[T] = puzzle.zipWithIndex.map {
      case (col, x) => col ++ p(x)
    }
  }

  def calcNextIndices[T <: Panel](left: Puzzle[T])(floatings: Puzzle[T]): Seq[(T, (Int, Int))] = {
    val appended = left append floatings
    floatings.flatten.map {
      p => (p, appended.indexOfPanelUnhandled(p))
    }
  }
}
