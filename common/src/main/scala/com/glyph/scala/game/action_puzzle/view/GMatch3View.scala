package com.glyph.scala.game.action_puzzle.view

import com.glyph.scala.game.action_puzzle.GMatch3._
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.glyph.scala.lib.util.Logging
import com.badlogic.gdx.scenes.scene2d._
import com.badlogic.gdx.scenes.scene2d.actions.{Actions, MoveToAction}
import com.glyph.scala.lib.util.updatable.task._
import scala.collection.mutable.ListBuffer
import com.badlogic.gdx.math.Interpolation
import com.glyph.scala.lib.libgdx.GdxUtil
import com.badlogic.gdx.math.MathUtils._
import com.badlogic.gdx.scenes.scene2d.actions.Actions._
import com.glyph.scala.game.action_puzzle.ActionPuzzle
import com.glyph.scala.lib.libgdx.actor._
import scalaz._
import Scalaz._
trait Grid extends WidgetGroup with Logging{
  val puzzleGroup = new Group with Scissor
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


  override def setSize(width: Float, height: Float){
    super.setSize(width,height)
   // val needSetup = getWidth != width || getHeight != height
    puzzleGroup.setWidth(getWidth - marginX)
    puzzleGroup.setHeight(getHeight - marginY)
    onSetupPosition()
  }

  def onSetupPosition() {
    setPosition(marginX / 2f, marginY / 2f)
  }
}

trait Paneled[T <: Actor] extends Grid with Updating {
  val sequencer = new SequentialProcessor {}
  this add sequencer
  val tokens = ListBuffer.empty[T]
  var swipeListener:Option[InputListener] = None
  def moveTokenToIndex(token: T, x: Int, y: Int, callback: () => Unit = () => {}) {
    val move = Actions.action(classOf[MoveToAction])
    move.setPosition(calcPanelX(x), calcPanelY(y))
    move.setDuration(0.5f)
    move.setInterpolation(Interpolation.exp10Out)
    token.addAction(sequence(move, run(new Runnable {
      def run() {
        callback()
      }
    })))
  }

  def setupNewPanel(panel: T, x: Int, y: Int) {
    //println("create panel token")
    val p = panel
    p.setTouchable(Touchable.disabled)
    //初期サイズの設定
    p.setSize(panelW, panelH)
    //拡大原点の設定
    p.setOrigin(p.getWidth / 2, p.getHeight / 2)
    //列の上部に初期配置する。
    p.setPosition(calcPanelX(x), calcPanelY(row + y))
    tokens += p
    GdxUtil.post {
      //追加処理はrenderスレッド前で
      puzzleGroup.addActor(p)
    }
  }

  def startSwipeCheck(callback:(Int,Int,Int,Int)=>Unit){
    swipeListener match{
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
              callback(current._1,current._2,next._1,next._2)
              current = next
            }
          }
        }
        addListener(listener)
        swipeListener = listener.some
      }

    }

  }
  def stopSwipeCheck(){
    swipeListener match{
      case Some(l) => {
        removeListener(l)
        swipeListener = None
      }
      case None => throw new IllegalStateException("swipeListener is not started yet!!!")
    }
  }

}

class GMatch3View[T <: Panel](ROW: Int, COLUMN: Int, panelSeed: T => GMatch3View.PanelToken[T]) extends Paneled[GMatch3View.PanelToken[T]] with Scissor with Logging {
  def row: Int = ROW

  def column: Int = COLUMN

  import ActionPuzzle._

  val panelMove: PanelMove[T] = (p, x, y) => new TA {
    var startX, startY = 0f
    var actor: Option[Actor] = None
    val targetX = calcPanelX(x)
    val targetY = calcPanelY(y)

    override def onStart() {
      super.onStart()
      actor = tokens.find(_.panel == p)
      for (a <- actor) {
        startX = a.getX
        startY = a.getY
      }
    }

    def update(alpha: Float) {
      for (a <- actor) {
        a.setPosition(startX + alpha * (targetX - startX), startY + alpha * (targetY - startY))
      }
    }

    override def onFinish() {
      super.onFinish()
      for (a <- actor) {
        a.setPosition(targetX, targetY)
      }
    }
  }

  val panelAdd: PanelAdd[T] = (p, x, y) => {
    val token = panelSeed(p)
    tokens += token
    addActor(token)
    token.setPosition(calcPanelX(x), calcPanelY(ROW + y))
    token.setSize(panelW,panelH)
    panelMove(p, x, y)
  }

  val panelRemove: PanelRemove[T] = p => {
    log("create panelRemove Animation")
    val tokenOpt = tokens.find(_.panel == p)
    for (token <- tokenOpt) {
      tokens -= token
    }
    new TA {
      override def onStart(){
        super.onStart()
        for(token<-tokenOpt)token.explode{
          log("explode!")
          token.remove()
        }
      }
      def update(alpha: Float) {
      }
    }
  }

  trait TA extends TimedAnimation {
    def onStart() {}
    def onFinish() {}
  }
}

object GMatch3View {
  trait PanelToken[T<:Panel]extends TouchSource with ExplosionFadeout{
    def panel:T
  }
}