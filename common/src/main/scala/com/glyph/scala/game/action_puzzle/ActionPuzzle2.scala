package com.glyph.scala.game.action_puzzle

import scalaz._
import Scalaz._
import com.glyph.scala.lib.util.Logging
import com.glyph.scala.game.action_puzzle.GMatch3.Panel
import com.glyph.scala.lib.util.reactive.Var
import com.badlogic.gdx.math.MathUtils
import com.glyph.scala.lib.util.updatable.task.ParallelProcessor

/**
 * @author glyph
 */
class ActionPuzzle2 extends Logging{
  import GMatch3._
  val processor = new ParallelProcessor{}
  val SIZE = 6
  val puzzle:Var[GMatch3.Puzzle[P]] = Var(Vector())
  val seed = ()=>MathUtils.random(0,5) |> (new P(_))
  def update(delta:Float){
    processor.update(delta)
  }
  def fill(){
    val filling = puzzle().createFilling(seed,SIZE)
    for((panel,x,y) <- filling){
      panel.ix() = x
      panel.iy() = y
    }
  }
  def updatePanels(delta:Float){
  }
}
class IntPanel(val n:Int) extends GMatch3.Panel{
  def matchTo(panel: Panel): Boolean = panel match{
    case p:IntPanel => p.n == n
    case _ => false
  }
}
class P(n:Int) extends IntPanel(n){
  val ix = Var(-1)
  val iy = Var(-1)
  val vx = Var(0f)
  val vy = Var(0f)
  val x = Var(0f)
  val y = Var(0f)
  val ax = Var(0f)
  var stopped = Var(false)
  def update(delta:Float){
    if(!stopped()){
      vx ()+= ax()*delta
      x ()+= vx()*delta
    }
  }
}
