package com.glyph.scala.game.action_puzzle

import com.glyph.scala.lib.puzzle.Match3._
import Animation._
import com.glyph.scala.game.action_puzzle.ActionPuzzle._
import com.glyph.scala.lib.util.reactive.Var
import com.glyph.scala.lib.util.updatable.task.ParallelProcessor
import com.glyph.scala.lib.util.updatable.reactive.Animator
import com.glyph.scala.lib.util.updatable.Updatables

/**
 * @author glyph
 */
class ActionPuzzle {
  val SIZE = 6
  var statics:Puzzle = ???
  var future:Puzzle = ???
  var floatings:Puzzle = ???
  val processor = new Updatables {}
  def seed():Panel = ???
  def update(dt:Float){}
  def panelFall(duration:Float,y:Int,p:Panel) = ???
  def userInput:Unit~>PuzzleInput = ???
  def idle:Unit~>GameResult = _=>cb=> {
    userInput(){
      case Swipe(ax,ay,bx,by) => {
        statics = statics.swap(ax,ay,bx,by)
        val scanned = statics.scanAll.flatten.map{case(p,x,y) =>p}.distinct
        val (left,floats) = statics.remove(scanned)
        val nextIndices = calcNextIndices(left)(floats)
        nextIndices foreach {
          case (p,(x,y)) =>
        }
        statics = left
        val lackingFuture = left append floats append floatings
        val filling = lackingFuture.createFillingPuzzle(seed,SIZE)
        future = lackingFuture append filling
        floatings = floats append floatings
        //TODO update the animation of floatings
        idle()(cb)
      }
    }
  }
  class APanel(i:Int) extends IntPanel(i) with ParallelProcessor{
    val y = Var(0f)
  }
  def fill(filling:Events){
    
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