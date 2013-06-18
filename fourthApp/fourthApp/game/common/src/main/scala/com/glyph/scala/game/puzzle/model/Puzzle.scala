package com.glyph.scala.game.puzzle.model

import panels.Panel
import panels.Panel.{Water, Fire, Thunder}
import scala._
import com.glyph.scala.lib.util.callback.Callback1
import scala.collection.mutable

/**
 * @author glyph
 */
class Puzzle {
  val ROW = 8
  val COLUMN = 8
  /**
   * should contain the data of puzzling panels
   **/
  val panels = Array((1 to COLUMN) map {
    _ => new mutable.ArrayBuffer[Panel]
  }: _*)
  // the right most column
  type Event = (Panel, Int, Int)
  val onPanelRemoved = new Callback1[Event]
  val onPanelAdded = new Callback1[Event]
  val onScan = new Callback1[(Seq[Event], Seq[Event])]

  def addPanel(x: Int) {
    val p = Panel.random()
    panels(x) += p
    onPanelAdded(p, x, panels(x).size - 1)
    //println("add"+p)
    //println(this)
  }

  def initPanel() {
    for (i <- 0 until COLUMN; j <- 0 until ROW) {
      addPanel(i)
    }
    // println(this)
  }

  /**
   * 縦横３つ並んでいるパネルを探し、消去する。
   */
  def scan() {
    import Math.min
    /*
    まず横についてチェック。
    次に縦についてチェック・・・
     */
    val scanned = (0 until min(COLUMN, panels.size)).map {
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
    scanned.foreach {
      case (p, x, y) => {
        panels(x) = panels(x).filter {
          _ ne p
        }
        //println("remove "+ p)
        //onPanelRemoved(p, x, y)
      }
    }
    onScan(scanned,fill())
    println(this)
  }

  def fill(): Seq[Event] = {
    for (x <- 0 until panels.size;i <- panels(x).size until ROW)yield {
      val p = Panel.random()
      panels(x) += p
      (p, x, i - 1)
    }
  }

  override def toString: String = {
    panels.map {
      col => col.map {
        case Thunder() => "T"
        case Water() => "W"
        case Fire() => "F"
        case _ => "?"
      }.fold("") {
        _ + "," + _
      }
    }.fold("") {
      _ + "\n" + _
    }
  }
}