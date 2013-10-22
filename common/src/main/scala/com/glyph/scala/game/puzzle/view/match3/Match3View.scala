package com.glyph.scala.game.puzzle.view.match3

import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.glyph.scala.lib.libgdx.actor.{TouchSource, Updating, Scissor}
import com.badlogic.gdx.scenes.scene2d.{Touchable, InputEvent, InputListener, Group}
import com.badlogic.gdx.scenes.scene2d
import scene2d.actions.{MoveToAction, Actions}
import com.glyph.scala.lib.util.updatable.task._
import com.glyph.scala.lib.libgdx.actor.action.Waiter
import com.badlogic.gdx.math.{MathUtils, Interpolation}
import com.glyph.scala.lib.util.reactive.{Var, Reactor, EventSource}
import scala.collection.mutable.ListBuffer
import com.glyph.scala.lib.libgdx.GdxUtil
import com.glyph.scala.game.puzzle.controller.PuzzleGameController
import scala.collection.mutable
import com.glyph.scala.lib.util.Logging
import com.glyph.scala.lib.puzzle.Match3


/**
 * @author glyph
 */
class Match3View(puzzle: Match3) extends WidgetGroup with Scissor with Updating with Reactor with TouchSource with Logging {

  import PuzzleGameController._
  import puzzle._
  import Match3._

  def marginX = getWidth / (COLUMN + COLUMN * 0.1f) * 0.1f

  def marginY = getHeight / (ROW + ROW * 0.1f) * 0.1f

  def panelW = getWidth / (COLUMN + COLUMN * 0.1f)

  def panelH = getHeight / (ROW + ROW * 0.1f)

  def divX = puzzleGroup.getWidth / COLUMN

  def divY = puzzleGroup.getHeight / ROW

  private def calcPanelX(x: Int): Float = divX * (x + 0.5f) - panelW / 2f

  private def calcPanelY(y: Int): Float = divY * (y + 0.5f) - panelH / 2f

  val puzzleGroup = new Group with Scissor

  this.addActor(puzzleGroup)

  import Actions._

  val sequencer = new SequentialProcessor {}
  val panelTouch = new EventSource[PanelToken]
  this add sequencer
  /*
  val tokens = Array(puzzle.rawPanels map {
    column => ListBuffer.empty[PanelToken]
  }: _*)
  */
  val tokens = ListBuffer.empty[PanelToken]

  //this should not be here
  val tokenExplosion = EventSource[PanelToken]()
  val afterSetupQueue = ListBuffer[() => Unit]()
  var processingSetup = false

  val visualSwipeLength = Var[Option[Int]](None)

  def postAfterSetup(f: => Unit) {
    if (processingSetup) afterSetupQueue += (() => f) else f
  }

  var puzzleBuffer: IndexedSeq[IndexedSeq[Panel]] = Vector()
  reactVar(puzzle.panels) {
    ps => puzzleBuffer = ps
  }

  var swipeListener: Option[InputListener] = None

  def startSwipeCheck(length: Int)(callback: Seq[(Int, Int, Int, Int)] => Unit) {
    //TODO make this not react while swiping...
    setTouchable(Touchable.enabled)
    swipeListener match {
      case Some(listener) => {
        error("swipe already started... or previous swipeCheck is not finished correctly. ignoring start swipeCheck")
      }
      case None => {
        visualSwipeLength() = Some(length)
        swipeListener = Some(new InputListener {
          val swipeRecord = mutable.Stack[(Int, Int, Int, Int)]()
          val record = mutable.Stack[(Int, Int)]()
          var current = (0, 0)
          val MAX_MOVE = length

          override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = {
            current = positionToIndex(x, y)
            record.clear()
            true
          }

          def diffAmount(a: (Int, Int), b: (Int, Int)) = Math.abs(a._1 - b._1) + Math.abs(a._2 - b._2)

          override def touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) {
            val next = positionToIndex(x, y)
            if (diffAmount(current, next) == 1 && (record.size < MAX_MOVE || (record.headOption exists {
              case head => head == next
            }))) {
              val ((a, b), (c, d)) = (current, next)
              record.headOption match {
                case Some(head) if head == next => swipeRecord.pop(); record.pop(); visualSwipeLength() = visualSwipeLength().map {
                  _ + 1
                }
                case _ => swipeRecord.push((a, b, c, d)); record.push(current); visualSwipeLength() = visualSwipeLength().map {
                  _ - 1
                }
              }
              puzzleBuffer = puzzleBuffer.swap(a, b, c, d)
              current = next
              setupTokenPosition()
            }
          }

          override def touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
            if (!record.isEmpty) {
              visualSwipeLength() = None
              postAfterSetup {
                callback(swipeRecord.reverse)
              }
            }
          }
        })
        swipeListener foreach addListener
      }
    }
  }

  def stopSwipeCheck() {
    setTouchable(Touchable.disabled)
    swipeListener foreach removeListener
    swipeListener = None
  }

  //TODO implement this animation.
  val fillAnimation: FillAnimation = {
    events => {
      callback => {
        events.foreach {
          case (p, x, y) => createPanelToken(p, x, y)
        }
        setupTokenPosition(callback = {
          callback()
        })
      }
    }
  }
  val destroyAnimation: DestroyAnimation = {
    events => {
      callback => {
        sequencer add Sequence(
          Parallel(
            events flatMap {
              case (p, x, y) => tokens collect {
                case token if token.panel eq p => {
                  //パネルのアニメ終了まで待つ
                  Wait(wait => {
                    //TODOなぜかrunがすぐに実行される問題=>implicit conversionのせい
                    token.explode {
                      token.remove()
                      tokens -= token
                      //println("puzzleView:remove!")
                      stopReact(token.press)
                      stopReact(token.drag)
                      stopReact(token.release)
                      token.dispose()
                      wait.wake()
                    }
                    tokenExplosion.emit(token)
                  })
                }
              }
            }: _*),
          Do {
            callback()
          })
      }
    }
  }

  import MathUtils.clamp

  def positionToIndex(px: Float, py: Float): (Int, Int) = (clamp((px / divX).toInt, 0, ROW - 1), clamp((py / divY).toInt, 0, COLUMN - 1))

  override def setWidth(width: Float) {
    if (getWidth != width) {
      super.setWidth(width)
      puzzleGroup.setWidth(Match3View.this.getWidth - marginX)
      setupPosition()
    }
  }

  override def setHeight(height: Float) {
    if (getHeight != height) {
      super.setHeight(height)
      puzzleGroup.setHeight(Match3View.this.getHeight - marginY)
      setupPosition()
    }
  }

  def setupPosition() {
    setPosition(marginX / 2f, marginY / 2f)
    setupTokenPosition()
  }

  def createPanelToken(panel: Panel, x: Int, y: Int) {
    //println("create panel token")
    val p = PanelToken(panel)
    p.setTouchable(Touchable.disabled)
    reactEvent(p.press) {
      // position in the token
      case (px, py) => {
        //println(p.getX, p.getY)
        //println(positionToIndex(p.getX, p.getY))
        //println(indexOf(p.panel))
        panelTouch.emit(p)
      }
    }
    //初期サイズの設定
    p.setSize(panelW, panelH)
    //拡大原点の設定
    p.setOrigin(p.getWidth / 2, p.getHeight / 2)
    //列の上部に初期配置する。
    p.setPosition(calcPanelX(x), calcPanelY(puzzle.ROW - puzzle.panels()(x).size + puzzle.ROW))
    tokens += p
    GdxUtil.post {
      //追加処理はrenderスレッド前で
      puzzleGroup.addActor(p)
    }
  }

  //TODO make this function work on buffer
  /**
   * 全てのトークンのアニメーション先を計算、設定、開始する。
   */
  def setupTokenPosition(targets: Seq[PanelToken] = tokens, callback: => Unit = {}) {
    //TODO puzzle modelからトークンのインデックスを取得
    processingSetup = true
    GdxUtil.post {
      val waiter = Waiter {
        processingSetup = false
        afterSetupQueue foreach {
          _()
        }
        afterSetupQueue.clear()
        callback
      }
      for (token <- targets) {
        puzzleBuffer.indexOfPanel(token.panel) match {
          case Right((ix, iy)) => {
            val move = action(classOf[MoveToAction])
            move.setPosition(calcPanelX(ix), calcPanelY(iy))
            move.setDuration(0.5f)
            move.setInterpolation(Interpolation.exp10Out)
            //token.clearActions()
            token.addAction(sequence(move, waiter.await()))
          }
          case Left(failed) => {
            failed.printStackTrace()
          }
        }
      }
    }
  }
}
