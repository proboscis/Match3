package com.glyph.scala.game.puzzle.controller

import com.glyph.scala.game.puzzle.view.{PanelToken, PuzzleView}
import com.glyph.scala.game.puzzle.model.Puzzle
import com.glyph.scala.lib.util.updatable.Updatable
import com.glyph.scala.lib.util.collection.list.{DoubleLinkedQueue, DoubleLinkedList}
import com.glyph.scala.lib.libgdx.actor.Touchable
import com.glyph.scala.lib.math.Vec2
import com.badlogic.gdx.scenes.scene2d.actions.{MoveToAction, Actions}
import com.badlogic.gdx.math.Interpolation
import com.glyph.scala.lib.util.observer.Observing
import com.glyph.scala.lib.util.Disposable
import com.glyph.scala.game.puzzle.model.panels.Panel

/**
 * @author glyph
 */
class PuzzleViewController(view: PuzzleView, puzzle: Puzzle) extends Updatable with Observing with Disposable {
  val tokens = Array(
    new DoubleLinkedQueue[PanelToken],
    new DoubleLinkedQueue[PanelToken],
    new DoubleLinkedQueue[PanelToken],
    new DoubleLinkedQueue[PanelToken],
    new DoubleLinkedQueue[PanelToken]
  )
  //TODO onInvalidate is not a good place to know when the view is initialized...
  puzzle.onPanelRemoved update {
    case (p, x, y) => {
      println(x+","+y+":"+p.getClass.getSimpleName)
      tokens.foreach {
        column => column.foreach {
          token =>
            if (token.panel eq p) {
              token.remove()
              column.remove(token)
            }
        }
      }
      setupTokenPosition()
    }
  }
  puzzle.onPanelAdded update {
    case (p, x, y) => {
      createPanelToken(p, x, y)
      setupTokenPosition()
    }
  }

  def createPanelToken(panel: Panel, x: Int, y: Int) {
    val p = new PanelToken(panel) with Touchable
    p.onPressed = (v: Vec2) => {
      println("touched")
      p.remove()
      tokens(x).remove(p)
      setupTokenPosition()
      //TODO remove from actual data
    }
    p.setSize(view.panelW, view.panelH)
    p.setPosition(calcPanelX(x),calcPanelY(puzzle.ROW - puzzle.panels(x).size + puzzle.ROW))
    tokens(x).enqueue(p)
    view.puzzleGroup.addActor(p)
  }
  private def calcPanelX(x:Int):Float = view.divX * (x + 0.5f) - view.panelW / 2f
  private def calcPanelY(y:Int):Float = view.divY * (y + 0.5f) - view.panelH / 2f
  /**
   * 全てのトークンのアニメーション先を計算、設定、開始する。
   */
  def setupTokenPosition() {
    var x = 0
    var y = 0
    while (x < 5) {
      val list = tokens(x)
      y = 0
      list.foreach {
        token => {
          val move = Actions.action(classOf[MoveToAction])
          move.setPosition(calcPanelX(x), calcPanelY(y))
          move.setDuration(3f)
          move.setInterpolation(Interpolation.exp10Out)
          token.clearActions()
          token.addAction(move)
          y += 1
        }
      }
      x += 1
    }
  }

  def dispose() {
    clearObserver()
  }
}
