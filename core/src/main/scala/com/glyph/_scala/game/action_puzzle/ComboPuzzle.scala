package com.glyph._scala.game.action_puzzle

import com.glyph._scala.lib.util.reactive.{FloatVar, Var}
import com.badlogic.gdx.math.MathUtils

/**
 * @author glyph
 */
class ComboPuzzle {
  /**
   * どのような面白さにするか・・・
   * プレイヤーの目的は、パネルを消すことなんですよ。
   * そこで、スコアは副次的なものなんですね
   * パネルを”うまく”消すのが、楽しいアクションパズルになるわけｓで
   */
  val puzzle = new ActionPuzzle(6, 6, () => MathUtils.random(0, 5), (a: Int, b: Int) => {
    a == b
  })
  val score = Var(0)
  val time = FloatVar(2f)// forget about the reactive programming!! the beauty of the implementation means nothing!!! the structure does matter, however.
  val combo = Var(0)
  var isGameOver = false
  def update(delta:Float){
    if(!isGameOver){
      time() -= delta
      puzzle.update(delta)
    }
    if(time() <= 0 && !isGameOver){
      isGameOver = true
      onGameOver()
    }
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
  var onGameOver = ()=>{

  }
}
