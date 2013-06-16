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
  val ROW = 5
  val COLUMN = 5
  /**
   * should contain the data of puzzling panels
   **/
  val panels = Array((1 to COLUMN) map {
    _ => new mutable.ArrayBuffer[Panel]
  }: _*)
  // the right most column
  val onPanelRemoved = new Callback1[(Panel, Int, Int)]
  val onPanelAdded = new Callback1[(Panel, Int, Int)]

  def addPanel(x: Int) {
    val p = Panel.random()
    panels(x) += p
    onPanelAdded(p, x, panels(x).size - 1)
    println("add"+p)
    println(this)
  }

  def initPanel() {
    for (i <- 0 until panels.length; j <- 1 to 5) {
      addPanel(i)
    }
    println(this)
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
      case (x,y)=>{
        (panels(x)(y), x, y)
      }
    }.foreach {
      case (p, x, y) =>{
        //本当はインスタンスチェックをして欲しい・・・
        panels(x) -= p//TODO ここでリムーブが内容チェックになっているっぽい
        println("remove "+ p)
        onPanelRemoved(p, x, y)
      }
    }
    fill()
  }

  def fill(){
    for (x <- 0 until panels.size){
      for ( i <- panels(x).size until ROW){
        addPanel(x)
      }
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