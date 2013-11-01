package com.glyph.scala.game.action_puzzle

import scala.annotation.tailrec
import scala.collection.mutable

/**
 * @author glyph
 */
object GMatch3 {

  import scala.util.control.Exception._

  trait Panel {
    def matchTo(panel: Panel): Boolean
  }

  type Event[T <: Panel] = (T, Int, Int)
  type MatchedSet[T <: Panel] = Seq[Event[T]]
  type Events[T <: Panel] = Seq[Event[T]]
  type Puzzle[T <: Panel] = IndexedSeq[IndexedSeq[T]]

  def included[T <: Panel](sets: MatchedSet[T], target: MatchedSet[T]): Boolean = target forall sets.contains

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

    //TODO scannerを使うと動かしたときに消せるパネルを表示できるようにしたいね
    def scan(x: Int, y: Int, right: Boolean): MatchedSet[T] = {
      val W = puzzle.size
      val H = puzzle.head.size
      val panel = puzzle(x)(y)
      var matching = (puzzle(x)(y), x, y) :: Nil
      //TODO check nonempty
      if (right) {
        var nx = x + 1
        while (nx < W &&  y < puzzle(nx).size && panel.matchTo(puzzle(nx)(y))) {
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

    def createFilling(seed: () => T, col: Int): Events[T] = for (x <- 0 until puzzle.size; y <- puzzle(x).size until col) yield (seed(), x, y)

    def createFillingPuzzle(seed: () => T,col:Int): Puzzle[T] = for (x <- 0 until puzzle.size) yield for (y <- puzzle(x).size until col) yield seed()

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
        col => col.span {
          var found = false
          (panel) => found || {
            found = !panels.contains(panel);
            found
          }
        } match {
          case (left, float) => (left filterNot f, float filterNot f)
        }
      }
    }

    def createNoMatchFilling(seed: () => T, col: Int): Events[T] = {
      val contains = puzzle.flatten.contains _
      @tailrec
      def fillWithStatics(added: Puzzle[T]): Puzzle[T] =
        added.createFilling(seed,col) match {
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

    def scanAll: Seq[MatchedSet[T]] = {
      val result = mutable.ArrayBuffer[MatchedSet[T]]()
      for {
        x <- 0 until puzzle.size
        y <- 0 until puzzle(x).size
        set <- true :: false :: Nil map (scan(x, y, _))} {
        var i = 0
        val l = result.length
        var noNeedToAdd = false
        while (i < l) {
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
      }
      //println(result)
      result
    }

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
