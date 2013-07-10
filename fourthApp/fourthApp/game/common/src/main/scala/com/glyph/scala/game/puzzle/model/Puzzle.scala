package com.glyph.scala.game.puzzle.model

import panels.Panel
import panels.Panel.{Monster, Water, Fire, Thunder}
import scala._
import scala.collection.mutable
import com.glyph.scala.lib.util.observer.Observable
import com.glyph.scala.lib.util.observer.reactive.EventSource

/**
 * @author glyph
 */
class Puzzle {
  import Puzzle._

  val ROW = 6
  val COLUMN = 6
  /**
   * should contain the data of puzzling panels
   **/
  val panels = Array((1 to COLUMN) map {
    _ => new mutable.ArrayBuffer[Panel]
  }: _*)

  val onPanelRemoved = new Observable[Events]
  val onPanelAdded = new Observable[Events]
  val panelRemoveEvent = new EventSource[Events]
  val panelAddEvent = new EventSource[Events]

  def canBeScanned = !scan().isEmpty

  /**
   * 縦横３つ並んでいるパネルを探し、返す
   */
  def scan(): Seq[Event] = {
    import Math.min
    /*
    まず横についてチェック。
    次に縦についてチェック・・・
     */
    (0 until min(COLUMN, panels.size)).map {
      x => (0 until min(ROW, panels(x).size)).map {
        y: Int => {
          {
            val c = panels(x)(y).getClass
            //横
            var index = x + 1
            while (index < min(panels.size, COLUMN) && y < panels(index).size && panels(index)(y).getClass == c) {
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
            val c = panels(x)(y).getClass
            var index = y + 1
            while (index < min(ROW, panels(x).size) && panels(x)(index).getClass == c) {
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
        (panels(x)(y), x, y)
      }
    }
  }

  def remove(events: Events) {
    events.foreach {
      case (p, x, y) => {
        panels(x) = panels(x).filter {
          _ ne p
        }
      }
    }
    onPanelRemoved(events)
    panelRemoveEvent.emit(events)
  }

  def createFilling: Events = {
    for (x <- 0 until panels.size; i <- panels(x).size until ROW) yield {
      val p = Panel.random()
      (p, x, i - 1)
    }
  }

  def fill(filling: Events) {
    filling.foreach {
      case (p, x, y) => panels(x) += p
    }
    onPanelAdded(filling)
    panelAddEvent.emit(filling)
  }

  override def toString: String = {
    panels.map {
      col => col.map {
        case _: Thunder => "T"
        case _: Water => "W"
        case _: Fire => "F"
        case _: Monster => "M"
        case _ => "?"
      }.fold("") {
        _ + "," + _
      }
    }.fold("") {
      _ + "\n" + _
    }
  }
}

object Puzzle {
  type Event = (Panel, Int, Int)
  type Events = Seq[Event]
}