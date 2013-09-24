package com.glyph.scala.game.puzzle.view.chain

import com.glyph.scala.game.puzzle.model.chain_puzzle.{ChainPanel, ChainPuzzle}
import com.glyph.scala.lib.libgdx.actor.Scissor
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.scenes.scene2d.Group
import com.glyph.scala.lib.libgdx.actor.action.Waiter
import com.badlogic.gdx.scenes.scene2d.actions.Actions._
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction
import com.badlogic.gdx.math.Interpolation
import com.glyph.scala.lib.libgdx.GdxUtil
import com.glyph.scala.lib.util.reactive.{Reactor, EventSource}
import scala.collection.immutable.Queue

/**
 * @author glyph
 */
class CPView(puzzle: ChainPuzzle) extends WidgetGroup with Scissor with Reactor {

  import puzzle._

  def marginX = getWidth / (COLUMN + COLUMN * 0.1f) * 0.1f

  def marginY = getHeight / (ROW + ROW * 0.1f) * 0.1f

  def panelW = getWidth / (COLUMN + COLUMN * 0.1f)

  def panelH = getHeight / (ROW + ROW * 0.1f)

  def divX = puzzleGroup.getWidth / COLUMN

  def divY = puzzleGroup.getHeight / ROW

  val puzzleGroup = new Group with Scissor

  this.addActor(puzzleGroup)

  val tokens = Array(puzzle.panels() map {
    seq => Queue.empty[CPToken]
  }: _*)
  val panelTouch = new EventSource[CPToken]

  override def setWidth(width: Float) {
    if (getWidth != width) {
      super.setWidth(width)
      puzzleGroup.setWidth(CPView.this.getWidth - marginX)
      setupPosition()
    }
  }

  override def setHeight(height: Float) {
    if (getHeight != height) {
      super.setHeight(height)
      puzzleGroup.setHeight(CPView.this.getHeight - marginY)
      setupPosition()
    }
  }

  def setupPosition() {
    setPosition(marginX / 2f, marginY / 2f)
    // println("x,y,w,h:" +(getX, getY, getWidth, getHeight))
    // println("marginX:" + marginX)
    setupTokenPosition()
  }

  def createPanelToken(panel: ChainPanel, x: Int, y: Int) {
    //println("create panel token")
    val p = new CPToken(panel)
    reactEvent(p.press) {
      pos => panelTouch.emit(p)
    }
    //初期サイズの設定
    p.setSize(panelW, panelH)
    //拡大原点の設定
    p.setOrigin(p.getWidth / 2, p.getHeight / 2)
    //列の上部に初期配置する。
    p.setPosition(calcPanelX(x), calcPanelY(puzzle.ROW - puzzle.panels()(x).size + puzzle.ROW))
    tokens(x) +:= p
    GdxUtil.post {
      puzzleGroup.addActor(p)
    }
  }

  private def calcPanelX(x: Int): Float = divX * (x + 0.5f) - panelW / 2f

  private def calcPanelY(y: Int): Float = divY * (y + 0.5f) - panelH / 2f

  def setupTokenPosition(f: => Unit = {}) {
    //TODO puzzle modelからトークンのインデックスを取得
    GdxUtil.post {
      () => println("setup")
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
