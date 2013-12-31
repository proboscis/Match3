package com.glyph.scala.game.action_puzzle

import com.glyph.scala.lib.util.reactive.Var
import com.badlogic.gdx.math.MathUtils

/**
 * @author glyph
 */
class ComboPuzzle {

  val puzzle = new ActionPuzzle(6, 6, () => MathUtils.random(0, 9), (a: Int, b: Int) => {
    a == b
  })
  val score = Var(0)
  val time = Var(60f)
  val combo = Var(0)
  def update(delta:Float){
    time() -= delta
    puzzle.update(delta + (score()/1000f/1000f))
  }
  var onPanelRemove = (seq:IndexedSeq[puzzle.AP])=>{}
  var onPanelAdd = (seq:IndexedSeq[IndexedSeq[puzzle.AP]])=>{}
  puzzle.panelRemove = seq => {
    combo() += seq.size
    score() += 10 * seq.size
    onPanelRemove(seq)
  }
  puzzle.panelAdd = seq=>{
    onPanelAdd(seq)
  }
}
