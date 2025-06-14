package com.glyph._scala.game.action_puzzle.view

import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.glyph._scala.lib.util.Logging
import com.badlogic.gdx.scenes.scene2d._
import com.badlogic.gdx.math.MathUtils._
import scalaz._
import Scalaz._
import com.glyph._scala.lib.util.json.JSON

class GridFunctions(nToken: Int, ratio: Int) {
  val margin = 1f / (nToken * (ratio + 1) + 1)
  val tokenSize = margin * ratio
  def alphaToIndex(i: Float): Int = ((i - margin / 2f) / ((ratio + 1) * margin)).toInt
  def indexToAlpha(a: Float): Float = (1 + a * (ratio + 1)) * margin
}

object Grid {
  def apply(json: JSON) = for {
    row <- json.row.asOpt[Int]
    column <- json.column.asOpt[Int]
    ratio <- json.ratio.asOpt[Int]
  } yield (new GridFunctions(row, ratio), new GridFunctions(column, ratio))
  def alphaToIndex(fx:GridFunctions,fy:GridFunctions) = (ax:Float,ay:Float)=>(fx.alphaToIndex(ax),fy.alphaToIndex(ay))
}

trait Grid extends WidgetGroup with Logging {
  val puzzleGroup = new Group
  this.addActor(puzzleGroup)

  def row: Int

  def column: Int

  def marginX = getWidth / (column + column * 0.1f) * 0.1f

  def marginY = getHeight / (row + row * 0.1f) * 0.1f

  def panelW = getWidth / (column + column * 0.1f)

  def panelH = getHeight / (row + row * 0.1f)

  def divX = puzzleGroup.getWidth / column

  def divY = puzzleGroup.getHeight / row

  def calcPanelX(x: Float): Float = divX * (x + 0.5f) - panelW / 2f

  def calcPanelY(y: Float): Float = divY * (y + 0.5f) - panelH / 2f

  def positionToIndex(px: Float, py: Float): (Int, Int) = (clamp((px / divX).toInt, 0, row - 1), clamp((py / divY).toInt, 0, column - 1))


  override def setWidth(width: Float) {
    if (getWidth != width) {
      super.setWidth(width)
      puzzleGroup.setWidth(getWidth - marginX)
      onSetupPosition()
    }
  }

  override def setHeight(height: Float) {
    if (getHeight != height) {
      super.setHeight(height)
      puzzleGroup.setHeight(getHeight - marginY)
      onSetupPosition()
    }
  }

  override def setSize(width: Float, height: Float) {
    super.setSize(width, height)
    // val needSetup = getWidth != width || getHeight != height
    puzzleGroup.setWidth(getWidth - marginX)
    puzzleGroup.setHeight(getHeight - marginY)
    onSetupPosition()
  }

  def onSetupPosition() {
    puzzleGroup.setPosition(marginX / 2f, marginY / 2f)
  }
}

trait Paneled extends Grid {
  var swipeListener: Option[InputListener] = None

  def startSwipeCheck(callback: (Int, Int, Int, Int) => Unit) {
    swipeListener match {
      case Some(l) => throw new IllegalStateException("swipe is already started!!!")
      case None => {
        val listener = new InputListener {
          var current = (0, 0)
          override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = {
            current = positionToIndex(x, y)
            true
          }
          def diffAmount(a: (Int, Int), b: (Int, Int)) = Math.abs(a._1 - b._1) + Math.abs(a._2 - b._2)
          override def touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) {
            val next = positionToIndex(x, y)
            if (diffAmount(current, next) == 1) {
              callback(current._1, current._2, next._1, next._2)
              current = next
            }
          }
        }
        addListener(listener)
        swipeListener = listener.some
      }
    }
  }

  def stopSwipeCheck() {
    swipeListener match {
      case Some(l) => {
        removeListener(l)
        swipeListener = None
      }
      case None => throw new IllegalStateException("swipeListener is not started yet!!!")
    }
  }
}
trait Paneled2 extends Group{
  var swipeListener: Option[InputListener] = None

  def genSwipeChecker(fx:GridFunctions,fy:GridFunctions):((Int,Int,Int,Int)=>Unit)=>Unit = {
    callback=>{
      swipeListener match {
        case Some(l) => throw new IllegalStateException("swipe is already started!!!")
        case None => {
          val listener = new InputListener {
            var current = (0, 0)
            override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = {
              current = (fx.alphaToIndex(x/getWidth),fy.alphaToIndex(y/getHeight))
              true
            }
            def diffAmount(a: (Int, Int), b: (Int, Int)) = Math.abs(a._1 - b._1) + Math.abs(a._2 - b._2)
            override def touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) {
              val next = (fx.alphaToIndex(x/getWidth),fy.alphaToIndex(y/getHeight))
              if (diffAmount(current, next) == 1) {
                callback(current._1, current._2, next._1, next._2)
                current = next
              }
            }
          }
          addListener(listener)
          swipeListener = listener.some
        }
      }
    }
  }
  def genSwipeStopper = ()=>{
    swipeListener match {
      case Some(l) => {
        removeListener(l)
        swipeListener = None
      }
      case None => //throw new IllegalStateException("swipeListener is not started yet!!!")
    }
  }
}