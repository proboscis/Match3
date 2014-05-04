package com.glyph._scala.game.action_puzzle

import com.badlogic.gdx.math.MathUtils
import com.glyph._scala.lib.libgdx.reactive.GdxFile
import com.glyph._scala.lib.util.Logging
import com.glyph._scala.lib.util.json.JSON
import com.glyph._scala.lib.util.reactive.{Var, Reactor, FloatVar}
import com.glyph._scala.social.SocialManager
import com.glyph._scala.lib.ecs.Scene
class ComboPuzzle extends Logging with Reactor {
  import ComboPuzzle._
  val config = GdxFile("comboPuzzle/config.json").map(_.map(JSON(_)))
  val timeConfig = config.map(_.map(_.time.as[Float]).flatten)
  val time = FloatVar(20f)
  reactSuccess(timeConfig)(time.update)
  val puzzle = new ActionPuzzle(6, 6, () => MathUtils.random(0, 5), (a: Int, b: Int) => {
    if(a == b) a else -1
  })
  val cross = Array((0,0),(1,0),(-1,0),(0,1),(0,-1))
  puzzle.markers += new Line3Matcher(puzzle.APFilter)
  puzzle.markers += new PatternMatcher[puzzle.AP](Array(cross),(a,b)=>puzzle.APFilter(a,b) != -1)
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
  /**
   * this is called every frame that has panels being removed
   * and is called twice for each fixed/falling part of the puzzle.
   * what if the pattern was partially falling?
   * when should the matched effects be applied?
   * pattern won't be removed at the same time, so its so hard to determine
   * that the pattern is removed...
   * solution is to stop removing falling panels.
   * 落下中に消えるべきか否か、これが問題だ。
   * 消えなかった場合、パターンの一部のみが消え、残りが発生する。
   * 残りがあってもいいものか？
   * 残りを無くすにはどうする必要があるか？
   * マッチ後の移動を不可能にする、もしくは落下中でも消える様にする。
   * 落下中に消えた場合、どのような問題があるか？
   * あるパターンが落下している場合、落下中はパターンとして認識しないようにし、
   * 落下終了後に消えた場合に初めてパターン認識とその結果を使うようにすればいいか
   * つまり、止まった状態で消えた場合のみパターンとして認識する様にすればいいか。
   */
  puzzle.panelRemove = seq => {
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