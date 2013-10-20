package com.glyph.scala.game.puzzle.model.match_puzzle

import scala.collection.mutable
import com.glyph.scala.lib.util.observer.Observable
import com.glyph.scala.lib.util.reactive.{Var, EventSource}
import scala.annotation.tailrec

/**
 * generic puzzle!
 * @author glyph
 */
class Match3(panelSeed: () => Panel, val ROW: Int = 6, val COLUMN: Int = 6) {
  import Match3._
  /**
   * should contain the data of puzzling panels
   **/
  val rawPanels = mutable.ArraySeq((1 to COLUMN) map {
    _ => new mutable.ArrayBuffer[Panel]
  }: _*)
  val panels = Var(Vector.empty[IndexedSeq[Panel]])
  val panelRemoveEvent = new EventSource[Events]
  val panelAddEvent = new EventSource[Events]

  def canBeScanned = !scan().isEmpty

  private def updatePanels() {
    //println(this)
    panels() = Vector(rawPanels.map {
      Vector(_: _*)
    }: _*)
  }

  def indexOf(panel: Panel): Option[(Int, Int)] = {
    import scala.util.control.Exception._
    allCatch opt {
      val row = rawPanels.collect {
        case buf if buf.contains(panel) => buf
      }.head
      (rawPanels.indexOf(row), row.indexOf(panel))
    }
  }

  def swap(ax: Int, ay: Int, bx: Int, by: Int) {
    val pa = rawPanels(ax)(ay)
    rawPanels(ax)(ay) = rawPanels(bx)(by)
    rawPanels(bx)(by) = pa
    updatePanels()
  }

  def findMatches: Seq[MatchedSet] = rawPanels.scanAll

  /**
   * 縦横３つ並んでいるパネルを探し、返す
   */
  def scan(): Events = {
    import Math.min
    /*
    まず横についてチェック。
    次に縦についてチェック・・・
     */
    (0 until min(COLUMN, rawPanels.size)).map {
      x => (0 until min(ROW, rawPanels(x).size)).map {
        y: Int => {
          {
            val c = rawPanels(x)(y)
            //横
            var index = x + 1
            while (index < min(rawPanels.size, COLUMN) && y < rawPanels(index).size && rawPanels(index)(y).matchTo(c)) {
              index += 1
            }
            if (index - x > 2) {
              List(x until index map {
                i => (i, y)
              }: _*)
            } else {
              List()
            }
          } ::: {
            //縦
            val c = rawPanels(x)(y)
            var index = y + 1
            while (index < min(ROW, rawPanels(x).size) && rawPanels(x)(index).matchTo(c)) {
              index += 1
            }
            if (index - y > 2) {
              List(y until index map {
                i => (x, i)
              }: _*)
            } else {
              List()
            }
          }
        }
      }.flatten
    }.flatten.distinct.map {
      case (x, y) => {
        (rawPanels(x)(y), x, y)
      }
    }
  }

  def remove(events: Events) {
    events.foreach {
      case (p, x, y) => {
        rawPanels(x) = rawPanels(x).filter {
          _ ne p
        }
      }
    }
    updatePanels()
    panelRemoveEvent.emit(events)
  }

  def removeIndices(request: (Int, Int)*) {
    var events: List[Event] = Nil
    request groupBy (_._1) foreach {
      case (key, group) => rawPanels(key) = rawPanels(key) diff group.map {
        case (x, y) => {
          events = (rawPanels(x)(y), x, y) :: events
          rawPanels(x)(y)
        }
      }
    }
    updatePanels()
    panelRemoveEvent.emit(events)
  }

  def createFilling: Events = {
    for (x <- 0 until rawPanels.size; i <- rawPanels(x).size until ROW) yield {
      (panelSeed(), x, i - 1)
    }
  }

  def createNoMatchFilling: Events = rawPanels.createNoMatchFilling(panelSeed, ROW)

  def fill(filling: Events) {
    filling.foreach {
      case (p, x, y) => rawPanels(x) += p
    }
    updatePanels()
    panelAddEvent.emit(filling)
  }

  override def toString: String = {
    rawPanels.map {
      col => col.map {
        _.toString
      }.fold("") {
        _ + "," + _
      }
    }.fold("") {
      _ + "\n" + _
    }
  }
}

object Match3 {

  import scala.util.control.Exception._

  type MatchedSet = Seq[Event]
  type Event = (Panel, Int, Int)
  type Events = Seq[Event]
  type Puzzle = IndexedSeq[IndexedSeq[Panel]]

  def included(sets: MatchedSet, target: MatchedSet): Boolean = target forall sets.contains

  //implicit def puzzleToImpl(puzzle:Puzzle):PuzzleImpl = new PuzzleImpl(puzzle)
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

    def fill(filling: Events): Puzzle = filling.foldLeft(puzzle) {
      case (p, (panel, x, _)) => p.updated(x, p(x) :+ panel)
    }

    def removePanels(panels: Seq[Panel]): Puzzle = puzzle.map {
      column => column.collect {
        case p if !panels.contains(p) => p
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
      def recFWM(p:Puzzle):Puzzle ={
        p.scanAll match{
          case Seq() => p
          case matches => recFWM(removePanels(matches.flatten.distinct.map {
            case (p, x, y) => p
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
     * @param panel
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

    /**
     * swaps the give indices of puzzle, and returns new swapped puzzle
     * @param ax
     * @param ay
     * @param bx
     * @param by
     * @return
     */
    def swap(ax: Int, ay: Int, bx: Int, by: Int): Puzzle = {
      val a = puzzle(ax)(ay)
      val b = puzzle(bx)(by)
      val puzzle1 = puzzle.updated(ax, puzzle(ax).updated(ay, b))
      val puzzle2 = puzzle1.updated(bx, puzzle1(bx).updated(by, a))
      puzzle2
    }

    //override def toString = Match3.toString(puzzle)
  }

}
