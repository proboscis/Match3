package com.glyph.scala.lib.puzzle

import scala.collection.mutable
import com.glyph.scala.lib.util.reactive.Var
import scala.annotation.tailrec
import scalaz._
import Scalaz._
import com.glyph.scala.lib.util.Logging
import scala.collection.mutable.ArrayBuffer

/**
 * generic puzzle!
 * @author glyph
 */
class Match3(val ROW: Int = 6, val COLUMN: Int = 6) extends Logging {
  type P = Match3.Panel

  import Match3._

  val panels: Var[IndexedSeq[IndexedSeq[P]]] = Var(Vector(1 to ROW map {
    _ => Vector.empty[P]
  }: _*))

  def indexOf(panel: P): Option[(Int, Int)] = panels().indexOfPanel(panel).fold(_ => None, pos => Some(pos))

  def swap(ax: Int, ay: Int, bx: Int, by: Int) {
    panels() = panels().swap(ax, ay, bx, by)
  }

  def findMatches: Seq[MatchedSet] = panels().scanAll

  def remove(events: Events) {
    panels() = panels().removePanels(events map {
      case (p, x, y) => p
    })
  }

  def removeIndices(request: (Int, Int)*) {
    val events = request map {
      case (x, y) => (panels()(x)(y), x, y)
    }
    panels() = (events map {
      case (p, x, y) => p
    }) |> panels().removePanels
  }

  def createFilling(seed: () => Panel): Events = panels().createFilling(seed, COLUMN)

  def createNoMatchFilling(seed: () => Panel): Events = panels().createNoMatchFilling(seed, ROW)

  def fill(filling: Events) {
    log("fill=>")
    log(panels().text)
    panels() = panels().fill(filling)
    log(panels().text)
    log("fill <=")
  }

  override def toString: String = panels().text
}

object Match3 {

  import scala.util.control.Exception._

  trait Panel {
    def matchTo(panel: Panel): Boolean
  }

  type Event = (Panel, Int, Int)
  type MatchedSet = Seq[Event]
  type Events = Seq[Event]
  type Puzzle = IndexedSeq[IndexedSeq[Panel]]

  def included(sets: MatchedSet, target: MatchedSet): Boolean = target forall sets.contains

  implicit class PuzzleImpl(val puzzle: Puzzle) extends AnyVal {
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
    def scan(x: Int, y: Int, right: Boolean): MatchedSet = {
      val W = puzzle.size
      val H = puzzle.head.size
      val panel = puzzle(x)(y)
      var matching = (puzzle(x)(y), x, y) :: Nil
      if (right) {
        var nx = x + 1
        while (nx < W && panel.matchTo(puzzle(nx)(y))) {
          matching ::=(puzzle(nx)(y), nx, y)
          nx += 1
        }
      } else {
        var ny = y + 1
        while (ny < H && panel.matchTo(puzzle(x)(ny))) {
          matching ::=(puzzle(x)(ny), x, ny)
          ny += 1
        }
      }
      if (matching.size >= 3) matching else Nil
    }

    def createFilling(seed: () => Panel, size: Int): Events = for (x <- 0 until puzzle.size; y <- puzzle(x).size - 1 until size - 1) yield (seed(), x, y)
    def createFillingPuzzle(seed:()=>Panel,size:Int):Puzzle = for (x <- 0 until puzzle.size)yield for( y <- puzzle(x).size - 1 until size - 1) yield seed()
    def fill(filling: Events): Puzzle = filling.foldLeft(puzzle) {
      case (p, (panel, x, _)) => p.updated(x, p(x) :+ panel)
    }

    def removePanels(panels: Seq[Panel]): Puzzle = puzzle.map {
      column => column.collect {
        case p if !panels.contains(p) => p
      }
    }

    /**
     * @param panels to be removed
     * @return (leftPanels, floatingPanels)
     */
    def remove(panels: Seq[Panel]): (Puzzle, Puzzle) = {
      val f = (p: Panel) => panels.contains(p)
      puzzle.unzip {
        col => col.span {
          var found = false
          (panel) => found || {
            found = !panels.contains(panel); found
          }
        } match {
          case (left,float) => (left filterNot f,float filterNot f)
        }
      }
    }

    def createNoMatchFilling(seed: () => Panel, size: Int): Events = {
      val contains = puzzle.flatten.contains _
      @tailrec
      def fillWithStatics(added: Puzzle): Puzzle =
        added.createFilling(seed, size) match {
          case filling => added.fill(filling) match {
            case filled => filled.scanAll match {
              case matches if matches.flatten.map(_._1).forall(contains) => filled
              case matches => fillWithStatics(added.removePanels(filling.filterNot(puzzle.flatten.contains).map {
                _._1
              }))
            }
          }
        }
      fillWithStatics(puzzle).zipWithIndex.flatMap {
        case (col, x) => col.zipWithIndex.collect {
          case (panel, y) if !puzzle(x).contains(panel) => (panel, x, y)
        }
      }
    }

    private def fillWithoutMatches(seed: () => Panel, size: Int): Puzzle = {
      @tailrec
      def recFWM(p: Puzzle): Puzzle = {
        p.scanAll match {
          case Seq() => p
          case matches => recFWM(removePanels(matches.flatten.distinct.map {
            case (pp, x, y) => pp
          }))
        }
      }
      recFWM(puzzle)
    }

    def scanAll: Seq[MatchedSet] = {
      val result = mutable.ArrayBuffer[MatchedSet]()
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
    def indexOfPanel(panel: Panel): Either[Throwable, (Int, Int)] = {
      //println(puzzle.flatten.contains(panel))
      allCatch either {
        val row = puzzle.filter {
          _.contains(panel)
        }.head
        (puzzle.indexOf(row), row.indexOf(panel))
      }
    }
    def indexOfPanelUnhandled(panel:Panel):(Int,Int) ={
      val row = puzzle.filter {
        _.contains(panel)
      }.head
      (puzzle.indexOf(row), row.indexOf(panel))
    }

    /**
     * swaps the give indices of puzzle, and returns new swapped puzzle
     * @return
     */
    def swap(ax: Int, ay: Int, bx: Int, by: Int): Puzzle = {
      val a = puzzle(ax)(ay)
      val b = puzzle(bx)(by)
      val puzzle1 = puzzle.updated(ax, puzzle(ax).updated(ay, b))
      val puzzle2 = puzzle1.updated(bx, puzzle1(bx).updated(by, a))
      puzzle2
    }

    def append(p:Puzzle):Puzzle = puzzle.zipWithIndex.map{
      case(col,x) => col ++ p(x)
    }
  }
  def calcNextIndices(left:Puzzle)(floatings:Puzzle):Seq[(Panel,(Int,Int))] = {
    val appended = left append floatings
    floatings.flatten.map{p => (p,appended.indexOfPanelUnhandled(p))}
  }
}
