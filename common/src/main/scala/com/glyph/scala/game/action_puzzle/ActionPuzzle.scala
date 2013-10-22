package com.glyph.scala.game.action_puzzle

import com.glyph.scala.lib.puzzle.Match3._
import Animation._
import com.glyph.scala.game.action_puzzle.ActionPuzzle._
import com.glyph.scala.lib.util.reactive.Var

/**
 * @author glyph
 */
class ActionPuzzle {
  val puzzle = Var(Vector(0 to 5 map Vector.empty[IntPanel] :_*))
  val setup: Unit ~> Unit = ???
  val idle: Unit ~> GameResult = ???
  val chaining: Unit ~> Unit = ???
  val start: Unit ~> GameResult = setup ~> idle
}

object ActionPuzzle {

  trait GameResult

  object GameOver extends GameResult

  case class IntPanel(n: Int) extends Panel {
    def matchTo(panel: Panel): Boolean = panel match {
      case IntPanel(i) => i == n
      case _ => false
    }
  }

  implicit def intToPanel(i: Int): Panel = IntPanel(i)
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