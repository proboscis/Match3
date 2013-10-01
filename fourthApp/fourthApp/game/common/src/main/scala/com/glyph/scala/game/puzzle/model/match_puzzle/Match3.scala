package com.glyph.scala.game.puzzle.model.match_puzzle

import scala.collection.mutable
import com.glyph.scala.lib.util.observer.Observable
import com.glyph.scala.lib.util.reactive.{Var, EventSource}

/**
 * generic puzzle!
 * @author glyph
 */
class Match3(panelSeed: () => Panel) {
  type Event = (Panel, Int, Int)
  type Events = Seq[Event]
  val ROW = 6
  val COLUMN = 6
  /**
   * should contain the data of puzzling panels
   **/
  val rawPanels = Array((1 to COLUMN) map {
    _ => new mutable.ArrayBuffer[Panel]
  }: _*)

  val panels = Var(Vector.empty[IndexedSeq[Panel]])

  val onPanelRemoved = new Observable[Events]
  val onPanelAdded = new Observable[Events]
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

  /**
   * 縦横３つ並んでいるパネルを探し、返す
   */
  def scan(): Seq[Event] = {
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
    onPanelRemoved(events)
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
    onPanelRemoved(events)
    panelRemoveEvent.emit(events)
  }

  def createFilling: Events = {
    for (x <- 0 until rawPanels.size; i <- rawPanels(x).size until ROW) yield {
      (panelSeed(), x, i - 1)
    }
  }

  def fill(filling: Events) {
    filling.foreach {
      case (p, x, y) => rawPanels(x) += p
    }
    updatePanels()
    onPanelAdded(filling)
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

  //Optionは存在するときのみ処理したい場合
  //Eitherは存在しなかった時の処理がある場合
  type Puzzle = IndexedSeq[IndexedSeq[Panel]]

  def puzzleToString(p: Puzzle): String = {
    p.map {
      col => col.map {
        _.toString
      }.fold("") {
        _ + "," + _
      }
    }.fold("") {
      _ + "\n" + _
    }
  }

  implicit def indexedSeqToPuzzleImpl(p: Puzzle): PuzzleImpl = new PuzzleImpl(p)

  class PuzzleImpl(puzzle: Puzzle) {
    def indexOfPanel(panel: Panel): Either[Throwable, (Int, Int)] = {
      //println(puzzle.flatten.contains(panel))
      allCatch either {
        val row = puzzle.filter {
          _.contains(panel)
        }.head
        (puzzle.indexOf(row), row.indexOf(panel))
      }
    }

    def swap(ax: Int, ay: Int, bx: Int, by: Int): Puzzle = {
      val a = puzzle(ax)(ay)
      val b = puzzle(bx)(by)
      val puzzle1 = puzzle.updated(ax, puzzle(ax).updated(ay, b))
      val puzzle2 = puzzle1.updated(bx, puzzle1(bx).updated(by, a))
      puzzle2
    }

    def toStr: String = puzzle.map {
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
