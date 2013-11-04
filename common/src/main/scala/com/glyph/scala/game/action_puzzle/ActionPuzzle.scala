package com.glyph.scala.game.action_puzzle

import com.glyph.scala.lib.util.updatable.task.{InterpolationTask, ParallelProcessor}
import com.badlogic.gdx.math.{Interpolation, MathUtils}
import scalaz._
import Scalaz._
import scala.collection.mutable.ListBuffer
import GMatch3._
import ActionPuzzle._
import com.glyph.scala.lib.util.reactive.Var
import com.glyph.scala.lib.util.Logging

/**
 * @author glyph
 */
class ActionPuzzle extends Logging {

  import Animation._

  type APuzzle = Puzzle[APanel]
  val SIZE = 6
  val statics: Var[APuzzle] = Var(Vector(0 until SIZE map {
    _ => Vector()
  }: _*))
  var future: Var[APuzzle] = Var(Vector(0 until SIZE map {
    _ => Vector()
  }: _*))
  var floatings: Var[APuzzle] = Var(Vector(0 until SIZE map {
    _ => Vector()
  }: _*))
  val panels = ListBuffer[APanel]()

  type Timed[P, R] = (Float => Unit) => (P => R) => Float => Unit

  def seed() = MathUtils.random(0, 2) |> (new APanel(_))

  def update(dt: Float) {
    panels foreach {
      _.update(dt)
    }
  }

  def userInput: Unit ~> PuzzleInput = ???

  def idle: Unit ~> GameResult = _ => cb => {
    userInput() {
      case Swipe(ax, ay, bx, by) => {
        statics() = statics().swap(ax, ay, bx, by)
        scanRemoveFill()
        idle()(cb)
      }
    }
  }

  def scanRemoveFill() {
    val scanned = statics().scanAll.flatten.map {
      case (p, x, y) => p
    }.distinct
    removePanels(scanned)
    fillPanels()
  }

  def initialize: Unit ~> GameResult = _ => cb => {
    log("initialize")
    fillPanels()
    idle(cb)
  }

  val dummy = new TimedAnimation {
    def onStart(): Unit = {}

    def onFinish(): Unit = {}

    def update(alpha: Float): Unit = {}
  }

  var panelAdd: PanelAdd[APanel] = (p, x, y) => dummy
  var panelMove: PanelMove[APanel] = (p, x, y) => dummy
  var panelRemove: PanelRemove[APanel] = (p) => dummy

  def removePanels(removing: Seq[APanel]) {
    //TODO do some explosion before removing tokens..
    removing foreach {
      p => p.add(new TimedHandler(panelRemove(p),()=>{
        log("remove animation")
        val (left, floats) = statics().remove(p::Nil)
        val nextIndices = calcNextIndices(left)(floats)
        nextIndices foreach {
          case (pp, (x, y)) =>{
            
          }
          //TODO update animation
        }
        statics() = left
        floatings() = floats append floatings()
        future() = left append floatings()
        panels -= p
        scanRemoveFill()
      }) in 0.3f)
    }
  }

  def fillPanels() {
    val filling = future().createFillingPuzzle(seed, SIZE)
    log("fillPanels:")
    log(filling.text)
    val flat = filling.flatten
    panels ++= flat
    future() = future() append filling
    // TODO calculate nice time for each panels
    import Interpolation._
    def tmp(f: (APanel, Int, Int) => TimedAnimation, target: Puzzle[APanel]) {
      target.flatten.foreach {
        p =>
          val (x, y) = future().indexOfPanelUnhandled(p)
          p.add(new TimedHandler(f(p, x, y), () => {
            statics() = statics().updated(x, statics()(x) :+ p)
            floatings() = floatings().updated(x, floatings()(x).filterNot(_ == p))
            scanRemoveFill()
          }) in 0.3f * y + 0.2f * x using exp10Out)
          log("added handler" +(p, x, y))
      }
    }
    tmp(panelAdd, filling)
    floatings() = floatings() append filling
  }

  //TODO 時間指定のanimationはdurationを1とした関数として定義すれば良い
  def updateFloatingAnimation(floats: Puzzle[APanel] = floatings()) {
    import Interpolation._
    var x = 0
    for (col <- floats) {
      var y = 0
      for (panel <- col) {
        val (fx, fy) = future().indexOfPanelUnhandled(panel)
        panel.clearAnimation()
        panel.add(new TimedHandler(panelMove(panel, fx, fy), () => {
          assert(statics()(x).contains(panel))
          statics() = statics().updated(x, statics()(x) :+ panel)
        }) in y * 0.3f using exp10Out)
        y += 1
      }
      x += 1
    }
  }
}

object ActionPuzzle {
  type PanelAdd[T <: Panel] = (T, Int, Int) => TimedAnimation
  type PanelMove[T <: Panel] = (T, Int, Int) => TimedAnimation
  type PanelRemove[T <: Panel] = (T) => TimedAnimation

  trait PuzzleInput

  case class Swipe(ax: Int, ay: Int, bx: Int, by: Int) extends PuzzleInput

  trait GameResult

  object GameOver extends GameResult

  class APanel(i: Int) extends IntPanel(i) with ParallelProcessor {
    def clearAnimation() = queuedTasks ++ startedTasks foreach removeTask
  }

  class IntPanel(val n: Int) extends Panel {
    def matchTo(panel: Panel): Boolean = panel match {
      case ip: IntPanel => ip.n == n
      case _ => false
    }

    override def toString: String = n + ""
  }

  implicit def intToPanel(i: Int): Panel = new IntPanel(i)

  trait TimedAnimation {
    def onStart()

    def onFinish()

    def update(alpha: Float)
  }

  class TimedHandler(timed: TimedAnimation, cb: () => Unit) extends InterpolationTask with Logging {
    override def onStart(): Unit = {
      super.onStart()
      timed.onStart()
    }

    override def onFinish(): Unit = {
      super.onFinish()
      timed.onFinish()
      cb()
    }

    def apply(alpha: Float): Unit = timed.update(alpha)
  }

}

object Animation {
  type Callback[R] = R => Unit
  type ~>[P, R] = P => Callback[R] => Unit
  trait AnimationOps[P, R] {
    def ~>[S](fb: R ~> S): P ~> S
  }

  implicit def animToOps[P, R](fa: P => (R => Unit) => Unit): AnimationOps[P, R] = new AnimationOps[P, R] {
    def ~>[S](fb: R ~> S): P ~> S = concat(fa, fb)
  }

  def concat[P, R, S](fa: P ~> R, fb: R ~> S): P ~> S = paramA => callback => fa(paramA)(fb(_)(callback))
}