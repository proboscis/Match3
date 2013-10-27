package com.glyph.scala.game.action_puzzle

import com.glyph.scala.lib.puzzle.Match3._
import Animation._
import com.glyph.scala.game.action_puzzle.ActionPuzzle._
import com.glyph.scala.lib.util.reactive.Var
import com.glyph.scala.lib.puzzle.Match3
import scala.math.Ordering.String

/**
 * @author glyph
 */
class ActionPuzzle {
  var puzzle:Puzzle = ???
  def update(dt:Float){}
  def panelFall(duration:Float,y:Int,p:Panel) = ???
  def userInput:Unit~>PuzzleInput = ???
  def idle:Unit~>GameResult = _=>cb=> {
    userInput(){
      case Swipe(ax,ay,bx,by) => {
        puzzle.swap(ax,ay,bx,by)
        val scanned = puzzle.scanAll.flatten.map{case(p,x,y) =>p}.distinct
        val (left,floating) = puzzle.remove(scanned)

      }
    }
  }
  class P{
    val timer = Var(0f)
  }
}

object ActionPuzzle {
  trait PuzzleInput
  case class Swipe(ax:Int,ay:Int,bx:Int,by:Int) extends PuzzleInput

  trait GameResult
  object GameOver extends GameResult
  class IntPanel(val n: Int) extends Panel {
    def matchTo(panel: Panel): Boolean = panel match {
      case  ip:IntPanel => ip.n == n
      case _ => false
    }

    override def toString:String = n + ""
  }

  implicit def intToPanel(i: Int): Panel = new IntPanel(i)
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