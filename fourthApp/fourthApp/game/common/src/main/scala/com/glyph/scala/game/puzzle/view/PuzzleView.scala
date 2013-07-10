package com.glyph.scala.game.puzzle.view

import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.glyph.scala.game.puzzle.model.Puzzle
import com.glyph.scala.lib.libgdx.actor.{Updating, Scissor}
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d
import com.glyph.scala.lib.util.observer.{Observable, Observing}
import panel.PanelToken
import scene2d.actions.{MoveToAction, Actions}
import com.glyph.scala.lib.util.updatable.task._
import com.glyph.scala.lib.util.collection.list.DoubleLinkedQueue
import com.glyph.scala.game.puzzle.model.panels.Panel
import com.badlogic.gdx.Gdx
import com.glyph.scala.lib.libgdx.actor.action.Waiter
import com.badlogic.gdx.math.Interpolation
import com.glyph.scala.game.puzzle.controller.PuzzleGameController

/**
 * @author glyph
 */
class PuzzleView(puzzle: Puzzle, controller: PuzzleGameController) extends WidgetGroup with Scissor with Updating with Observing {

  import puzzle._

  def marginX = getWidth / (COLUMN + COLUMN * 0.1f) * 0.1f

  def marginY = getHeight / (ROW + ROW * 0.1f) * 0.1f

  def panelW = getWidth / (COLUMN + COLUMN * 0.1f)

  def panelH = getHeight / (ROW + ROW * 0.1f)

  def divX = puzzleGroup.getWidth / COLUMN

  def divY = puzzleGroup.getHeight / ROW

  val puzzleGroup = new Group with Scissor

  this.addActor(puzzleGroup)

  override def setWidth(width: Float) {
    super.setWidth(width)
    puzzleGroup.setWidth(PuzzleView.this.getWidth - marginX)
    setupPosition()
  }

  override def setHeight(height: Float) {
    super.setHeight(height)
    puzzleGroup.setHeight(PuzzleView.this.getHeight - marginY)
    setupPosition()
  }

  def setupPosition() {
    setPosition(marginX / 2f, marginY / 2f)
   // println("x,y,w,h:" +(getX, getY, getWidth, getHeight))
   // println("marginX:" + marginX)
    setupTokenPosition()
  }

  import Actions._
  import com.glyph.scala.lib.util.Implicit._

  val sequencer = new SequentialProcessor {}
  val panelTouch = new Observable[PanelToken]
  this add sequencer
  val tokens = Array(puzzle.panels map {
    column => new DoubleLinkedQueue[PanelToken]
  }: _*)

  observe(puzzle.onPanelRemoved) {
    events =>
      sequencer add Sequence(
        Parallel(
          events flatMap {
            case (p, x, y) => tokens(x) filter {
              token => token.panel eq p
            } map {
              token =>
              //パネルのアニメ終了まで待つ
                Wait(wait => {
                  //TODOなぜかrunがすぐに実行される問題=>implicit conversionのせい
                  token.explode {
                    token.remove
                    tokens(x).remove(token)
                    removeObserver(token.press)
                    wait.wake()
                  }
                })
            }
          }: _*),
        Do {
            controller.onRemoveAnimationEnd()

        })
  }

  observe(puzzle.onPanelAdded) {
    events => events.foreach {
      case (p, x, y) => createPanelToken(p, x, y)
    }
    setupTokenPosition {
      controller.onFillAnimationEnd()
    }
  }

  def createPanelToken(panel: Panel, x: Int, y: Int) {
    //println("create panel token")
    val p = PanelToken(panel)
    observe(p.press){
      pos => panelTouch(p)
    }
    //初期サイズの設定
    p.setSize(panelW, panelH)
    //拡大原点の設定
    p.setOrigin(p.getWidth / 2, p.getHeight / 2)
    //列の上部に初期配置する。
    p.setPosition(calcPanelX(x), calcPanelY(puzzle.ROW - puzzle.panels(x).size + puzzle.ROW))
    tokens(x).enqueue(p)
    Gdx.app.postRunnable {
      () =>
      //追加処理はrenderスレッド前で
        puzzleGroup.addActor(p)
    }
  }

  private def calcPanelX(x: Int): Float = divX * (x + 0.5f) - panelW / 2f

  private def calcPanelY(y: Int): Float = divY * (y + 0.5f) - panelH / 2f

  /**
   * 全てのトークンのアニメーション先を計算、設定、開始する。
   */
  def setupTokenPosition(f: => Unit = {}) {
    Gdx.app.postRunnable {
      () =>
        val waiter = Waiter(f)
        var x = 0
        var y = 0
        while (x < puzzle.COLUMN) {
          val list = tokens(x)
          y = 0
          list.foreach {
            token => {
              val move = action(classOf[MoveToAction])
              move.setPosition(calcPanelX(x), calcPanelY(y))
              move.setDuration(0.5f)
              move.setInterpolation(Interpolation.exp10Out)
              //token.clearActions()
              token.addAction(sequence(move, waiter.await()))
              y += 1
            }
          }
          x += 1
        }
    }
  }
}
