package com.glyph._scala.game.action_puzzle

import com.badlogic.gdx.math.MathUtils
import com.glyph._scala.lib.libgdx.reactive.GdxFile
import com.glyph._scala.lib.util.Logging
import com.glyph._scala.lib.util.json.JSON
import com.glyph._scala.lib.util.reactive.{Var, Reactor, FloatVar}
import com.glyph._scala.social.SocialManager
import com.glyph._scala.lib.ecs.Scene
import com.glyph.ClassMacro._
import com.glyph._scala.lib.event.EventManager
case class PanelRemove(var removed:Seq[ActionPuzzle[_]#AP])
class ComboPuzzle extends Logging with Reactor {
  import ComboPuzzle._
  val events = new EventManager
  val config = GdxFile("comboPuzzle/config.json").map(_.map(JSON(_)))
  val timeConfig = config.map(_.map(_.time.as[Float]).flatten)
  val time = FloatVar(20f)
  reactSuccess(timeConfig)(time.update)
  val puzzle = new ActionPuzzle(6, 6, () => MathUtils.random(0, 5), (a: Int, b: Int) => {
    if(a == b) a else -1
  })
  puzzle.markers += new Line3Matcher(puzzle.APFilter)
  puzzle.markers += new PatternMatcher[puzzle.AP](Array(PanelPattern.distinctPatterns:_*),(a,b)=>puzzle.APFilter(a,b) != -1)
  val decoder = new PanelPatternDecoder(events)
  val score = Var(0)
  val heat = FloatVar(0f)//レベルに応じたヒートが貯まると、レベルが上がる
  val level = Var(1)
  val requiredHeat = Var(100f)
  heat += (v=>{
    if(v >= requiredHeat()){
      level() += 1
      heat () -= requiredHeat()
    }
  })
  reactVar(level)(err)
  private var isGameOver = false
  def update(delta: Float) {
    if (!isGameOver) {
      time() -= delta
      puzzle.update(delta)
    }
    if (time() <= 0 && !isGameOver) {
      isGameOver = true
      onGameOver()
    }
  }
  private def onGameOver(){
    SocialManager.manager.submitScore(score().toLong)
    val newScores = LocalLeaderBoard.load(LOCAL_LEADERBOARD) :+ (System.currentTimeMillis() -> score().toLong)
    LocalLeaderBoard.save(LOCAL_LEADERBOARD, newScores)
    onGameOverCallback()
  }
  var onPanelRemove = (seq: IndexedSeq[puzzle.AP]) => {}
  var onPanelAdd = (seq: IndexedSeq[IndexedSeq[puzzle.AP]]) => {}
  /**
   * panel is destroyed with heat and score
   * heat varies according to some other variables such as heat gauge.
   */
  var onPanelScore:(puzzle.AP,Int) => Unit = (_,_)=>{}
  puzzle.panelRemove = seq => {
    /**
     * エフェクトはプレイヤの状態によって変更されるし、
     * 形状の認識も変更される可能性がある・・・
     * どのように実装すべきか？
     * 移譲してイベントシステムに任せる！
     */
    events << PanelRemove(seq)//throw global event
    val size = seq.size
    heat() += size*size
    var i = 0
    while(i < size){
      val pScore = (1 * level()).toInt
      score() += pScore
      onPanelScore(seq(i),pScore)
      i += 1
    }
    onPanelRemove(seq)
  }
  puzzle.panelAdd = seq => {
    onPanelAdd(seq)
  }
  var onGameOverCallback = () => {}
}
object ComboPuzzle {
  val LOCAL_LEADERBOARD = getClass.getCanonicalName + "local_leaderboard.json"
}