package com.glyph._scala.game.action_puzzle

import com.glyph._scala.lib.util.reactive.{FloatVar, Var}
import com.badlogic.gdx.math.Interpolation
import com.glyph._scala.lib.util.updatable.task._
import com.glyph._scala.lib.util.{HeapMeasure, Timing, Logging}
import scala.collection.mutable.ArrayBuffer
import com.glyph._scala.lib.util.pool.{Pool, Pooling}
import com.glyph._scala.game.Glyphs
import Glyphs._

trait Marker[T]{
  import ActionPuzzle._
  def mark(puzzle:Puzzle[T],row:Int,col:Int,dst:PatternBuffer[T],allocator:()=>PanelBuffer[T])
}

/**
 * removedされたパネルのパターンを計算する。
 * （同時計算された物のみ考える）
 * puzzle全体からパターンを探すクラスと、
 * 与えられたパネル達の中にパターンが存在するかをチェックするクラスが必要。
 * このためには、合体したパターンのタイマーが確実に同期されている必要もある。
 * したがって、preMatch()でマッチした場合はタイマーの同期を行う。
 * Sync!等の　エフェクトや、パネルの大きさや明度で残り時間を表現するい必要がある。
 * つまり、流れとしては
 * swipe->
 * preMatch->sync(double check)
 * preRemoveMatch->effect
 * remove
 * もちろん、全ての動作の間にユーザーの操作が入り、状況が変化する可能姓もあるため慎重に。
 * どうやってsyncする？
 * 新たなマッチのタイマーがが前のマッチよりも短いことが無い限り、同期は保たれている。
 * 追加されるとすれば上からなので、縦にsyncしてから横にsyncすれば大丈夫
 * だが、もしパターンが３マッチに囚われていなかった場合、
 * これでは上手くいかない
 * 完璧にするには、マッチした瞬間にsyncを取る?
 * つまり、３という制限を排除し、外部に委譲する必要があるということ。
 * 同時に消えるべきか否か、これが暗黙のルールだった。
 * そして、同時に消えるべきということがわかった今、どうするか？
 * そう、往復してsyncをとる必要がある。
 * マッチした履歴をとり、その履歴を往復してsyncする。
 * right! problem solved. isn't it?
 */

/**
 * 役割を減らす。
 * 落とす、消す、時限消滅
 * filter function must return -1 if its not matching, and should return group id if it's matching
 * @author glyph
 */
class ActionPuzzle[T](val ROW: Int, val COLUMN: Int, seed: () => T, filterFunction: (T, T) => Int)
  extends Logging
  with Timing
  with HeapMeasure {
  //why does AP have to be an inner class?
  //You cannot define an outer class inside of this class!! ofcourse, ofcourse...
  type PanelBuffer = ActionPuzzle.PanelBuffer[AP]//since this is inner class, making things difficult....
  type PuzzleBuffer = ActionPuzzle.PuzzleBuffer[AP]
  type PatternBuffer = ActionPuzzle.PatternBuffer[AP]
  type Puzzle = ActionPuzzle.Puzzle[AP]
  type Panels = ActionPuzzle.Panels[AP]
  //TODO reactiveのautoBoxing
  log("new ActionPuzzle")
  //NOTE 初回起動時に中断し再度開始するとfalling状態のパネルが戻らなくなり、パズルを再生成しても改善されない問題=>
  //Poolingを判別するタグがインナークラス同士で共有されてしまい、違うパズルで同じfutureを参照していたために発生した
  //TODO trailではなく、terrariaのmolten armor のようなパーティクルエフェクトを軌跡に使うべき
  /**
   * 時間差でマッチ
   * 時間で敵が攻撃してくる
   * 剣と敵のマッチ
   * パネルの効果設定
   */
  //callbacks
  /**
   * invoked when the panel is added
   */
  var panelAdd = (panels: Puzzle) => {}
  /**
   * invoked when the panels are removed
   * panels are valid until this function returns.
   * so do the final pattern match at this callback.
   */
  var panelRemove = (panels: Panels) => {}
  /**
   * the result of this function will be regarded as patterns and
   * will be synchronized of their timer
   * this function is supposed to concat the marked panels set to given buffer.
   */
  val markers = ArrayBuffer[Marker[AP]]()

  import GMatch3._


  private implicit object PoolingPuzzle extends Pooling[PuzzleBuffer] {
    val generator = new IndexedSeqGen[ArrayBuffer] {
      def convert[T](seq: Seq[T]) = ArrayBuffer.apply(seq: _*)
    }

    def newInstance = GMatch3.initialize[AP, ArrayBuffer](COLUMN)(generator)

    val cleaner = (buf: ArrayBuffer[AP]) => buf.clear()

    def reset(tgt: PuzzleBuffer) = tgt foreach cleaner //the profile shows this is allocating... but how???
  }

  private implicit object PoolingAP extends Pooling[AP] {
    def newInstance: ActionPuzzle.this.type#AP = new AP

    def reset(tgt: ActionPuzzle.this.type#AP): Unit = tgt.reset()
  }

  private implicit val APPool = Pool[AP](1000)
  private implicit val puzzlePool = Pool[PuzzleBuffer](100)
  private implicit val parallelPool = Pool[Parallel](100)
  private implicit val sequencePool = Pool[Sequence](100)
  private implicit val interpolatorPool = Pool[Interpolator[AP]](100)
  private implicit val arrayBufferAPPool = Pool(() => ArrayBuffer[AP]())(_.clear())(100)

  val initialMatchingTime = Var(1f)
  val gravity = -10f
  private val processor = new ParallelProcessor {}

  private val APSeed: () => AP = () => {
    val np = manual[AP]//freed in panel resetter
    np.value = seed()
    np
  }
  private val fixed = manual[PuzzleBuffer]
  private val falling = manual[PuzzleBuffer]
  private val future = manual[PuzzleBuffer]

  val APFilter = (a: AP, b: AP) => {
    if (a != null && b != null && !a.isSwiping() && !b.isSwiping()) {
      filterFunction(a.value, b.value)
    } else {
      -1
    }
  }

  private val freePanelBuffer = (buf:PanelBuffer) => buf.free
  private val obtainPanelBuffer = ()=>manual[PanelBuffer]
  private val synchronizeTimer = (panels:IndexedSeq[AP])=>{
    //is initial matching time required? yes, it is...
    var chmp = initialMatchingTime()
    var i = 0
    val size = panels.size
    while(i < size){
      val p = panels(i)
      val t = p.matchTimer()
      if(0 < t && t < chmp) chmp = t
      i += 1
    }
    i = 0
    while(i < size){
      val p = panels(i)
      assert(!(0f < p.matchTimer() && chmp == 0f))
      p.matchTimer()= chmp//should matchTimer be varying?
      //or floatVar? i think this can be just a float
      i+=1
    }
  }
  //this is called when the panels is swiped or any panel finished falling
  //should be called whenever the fixed buffer changes.
  
  private val markBuffer = ArrayBuffer[ArrayBuffer[AP]]()
  private def scanAndMark() {
    val buf = manual[PuzzleBuffer]
    GMatch3.fixedFuture(fixed, future, buf)
    //TODO change the scanner(i mean marker)
    //TODO if you want to make the marker out of class, you have to make the type of Marker out of this class or it'll get very complicated
    // and it won't be able to make it without knowing the existence of this class
    //matcher.scanAll(buf, ROW, COLUMN, APFilter, synchronizeTimer)
    //matcher should return the set of matched patterns

    {//call all the markers
      var i = 0
      val size = markers.size
      while(i < size){
        //add marked patterns to the markBuffer
        markers(i).mark(buf,ROW,COLUMN,markBuffer,obtainPanelBuffer)
        i+=1
      }
      //sync twice to fully sync
      markBuffer foreach synchronizeTimer
      markBuffer foreach synchronizeTimer
      markBuffer foreach freePanelBuffer // free allocated panelBuffer
      markBuffer.clear()//clear marked buffer
    }
    buf.free
  }

  private def verified(x: Int)(y: Int)(nx: Int)(ny: Int) =
    0 <= x && x < ROW && 0 <= y && y < COLUMN &&
      0 <= nx && nx < ROW && 0 <= ny && ny < COLUMN &&
      y < fixed(x).size && ny < fixed(nx).size

  def pooledSwipe(x: Int, y: Int, nx: Int, ny: Int) {
    if (verified(x)(y)(nx)(ny)) {
      val par = auto[Parallel]
      val seq = auto[Sequence]
      val pa = future(x)(y)
      val pb = future(nx)(ny)
      val taskA = swipeAnimation(nx, ny, pa)
      val seqA = auto[Sequence]
      seqA.addTask(taskA)
      seqA.addTask(Block {
        //TODO fix ALLOCATION!!!
        pa.swipeAnimation -= taskA
      })
      val taskB = swipeAnimation(x, y, pb)
      val seqB = auto[Sequence]
      seqB.addTask(taskB)
      seqB.addTask(Block {
        //TODO fix ALLOCATION!!!
        pb.swipeAnimation -= taskB
      })
      pa.swipeAnimation += taskA
      pb.swipeAnimation += taskB
      par.addTask(seqA)
      par.addTask(seqB)
      seq.addTask(par)
      seq.addTask(Block {
        //TODO ALLOCATION!
        if (verified(x)(y)(nx)(ny)) {
          GMatch3.swap(fixed, x, y, nx, ny)
          scanAndMark()
        }
      })
      processor.addTask(seq)
      GMatch3.swap(future, x, y, nx, ny)
      updateTargetPosition()
    }
  }

  private object AP_XY extends Accessor[AP] {
    def size: Int = 2

    def get(tgt: ActionPuzzle.this.type#AP, values: Array[Float]) {
      values(0) = tgt.x()
      values(1) = tgt.y()
    }

    def set(tgt: ActionPuzzle.this.type#AP, values: Array[Float]) {
      tgt.x() = values(0)
      tgt.y() = values(1)
    }
  }

  def swipeAnimation(nx: Int, ny: Int, p: AP): Task = {
    import Interpolation._
    val api = auto[Interpolator[AP]]
    api set p of AP_XY to(nx, ny) in 0.3f using exp10Out
  }

  private val fallingSetter = (ap: AP) => ap.isFalling() = true
  private val seqFallingSetter = (seq: Seq[AP]) => seq foreach fallingSetter
  private def setFallingFlag() {
    falling foreach seqFallingSetter
  }

  private val nonEmpty = (seq: Seq[Any]) => !seq.isEmpty

  def fill() {
    //TODO remove allocation.
    //log("fill")
    val filling = manual[PuzzleBuffer]
    GMatch3.createFillingPuzzle2(future)(APSeed)(ROW, COLUMN)(filling)
    //val filling = future createFillingPuzzle(APSeed, COLUMN) //no cost
    if (filling.exists(nonEmpty)) {
      //TODO exist method allocates annon fun
      //printTime("fill:update failling"){
      val nextFall: PuzzleBuffer = manual[PuzzleBuffer]
      copy(falling)(nextFall)
      append(filling)(nextFall)
      //log("fill:setfalling")
      PoolingPuzzle.reset(falling)
      copy(nextFall)(falling)
      setFallingFlag()
      nextFall.free
      //falling() = falling() append filling // howmuch?
      //}
      //printTime("fill:update future"){
      val nextFuture: PuzzleBuffer = manual[PuzzleBuffer]
      copy(fixed)(nextFuture)
      append(falling)(nextFuture)
      //log("fill:setFuture")
      PoolingPuzzle.reset(future)
      copy(nextFuture)(future)
      nextFuture.free
      //future() = fixed() append falling() //costs 1ms
      placePanelsAbovePuzzle(filling)
      panelAdd(filling)
    }
    filling.free
  }

  private def placePanelsAbovePuzzle(buf: PuzzleBuffer) {
    var x = 0
    val width = buf.length
    while (x < width) {
      val row = buf(x)
      val height = row.length
      var y = 0
      while (y < height) {
        val p = row(y)
        p.x() = x
        p.y() = COLUMN + y
        y += 1
      }
      x += 1
    }
  }


  //trying to avoid allocation here...
  private val panelResetter = (p: AP) => p.free
  private val cancelSwipingAnimation = (panel: AP) => {
    //TODO set a panel to appropriate position
    //TODO free the canceled animation
    panel.swipeAnimation.clear()
  }
  private val bufCanceller = (buf: ArrayBuffer[AP]) => buf.foreach(cancelSwipingAnimation)

  /**
   * removes panels and update all the falling and fixed and future buffer of this puzzle.
   * @param panels fixed panels to be removed
   * @param fallings falling panels to be removed
   */
  def removeFillUpdateTargetPosition(panels: IndexedSeq[AP], fallings: IndexedSeq[AP]) {
    if (!panels.isEmpty || !fallings.isEmpty) {
      {
        val fixedTemp = manual[PuzzleBuffer]
        val fallingTemp = manual[PuzzleBuffer]
        val ffBuf = manual[PuzzleBuffer]
        val rmfTmp1 = manual[PuzzleBuffer]
        val rmfTmp2 = manual[PuzzleBuffer]

        fixedFuture(fixed, future, ffBuf)
        remove(ffBuf)(fixedTemp)(fallingTemp)(panels)
        remove(falling)(rmfTmp1)(rmfTmp2)(fallings)
        append(rmfTmp2)(rmfTmp1)
        copy(rmfTmp1)(falling)
        fallingTemp.foreach(bufCanceller)
        append(falling)(fallingTemp)
        copy(fallingTemp)(falling)
        setFallingFlag()
        panelRemove(panels)//calling back!
        panelRemove(fallings)
        panels.foreach(panelResetter)
        copy(fixedTemp)(fixed)
        fixedTemp.free
        fallingTemp.free
        ffBuf.free
        rmfTmp1.free
        rmfTmp2.free
      }

      {
        val futureTemp = manual[PuzzleBuffer]
        copy(fixed)(futureTemp)
        append(falling)(futureTemp)
        // log("remove:setFuture")
        copy(futureTemp)(future)
        futureTemp.free
      }
      /*
      fixed() = left //wait中にfixedを変更するとバグが発生するおそれがある。　要注意
      falling() = fallen append falling()
      future() = fixed() append falling()
      */
    }
    fill()
    updateTargetPosition()
  }

  def updateTargetPosition() {
    var x = 0
    val ft = future
    val width = ft.length
    while (x < width) {
      val row = ft(x)
      val height = row.length
      var y = 0
      while (y < height) {
        val p = row(y)
        p.tx = x
        p.ty = y
        y += 1
      }
      x += 1
    }
  }

  def initialize() {
    fill()
    updateTargetPosition()
  }

  def update(delta: Float) {
    updateFalling(delta)
    processor.update(delta)
    updateMatches(delta)
  }

  val matchRemoveBuf = ArrayBuffer.empty[AP]
  val fallingRemoveBuf = ArrayBuffer.empty[AP]

  private def updateMatches(delta: Float) {
    {
      var x = 0
      val width = fixed.size
      while (x < width) {
        val row = fixed(x)
        val height = row.size
        var y = 0
        while (y < height) {
          val panel = row(y)
          if (panel.isMatching) {
            if (panel.updateMatch(delta)) {
              matchRemoveBuf += panel
            }
          }
          y += 1
        }
        x += 1
      }
    }
    {
      var x = 0
      val width = falling.size
      while (x < width) {
        val row = falling(x)
        val height = row.size
        var y = 0
        while (y < height) {
          val p = row(y)
          if (p.isMatching) {
            if (p.updateMatch(delta)) fallingRemoveBuf += p
          }
          y += 1
        }
        x += 1
      }
    }
    if (!matchRemoveBuf.isEmpty || !fallingRemoveBuf.isEmpty) {
      removeFillUpdateTargetPosition(matchRemoveBuf, fallingRemoveBuf)
    }
    fallingRemoveBuf.clear()
    matchRemoveBuf.clear()
  }

  //for optimization
  val finishedBuf = ArrayBuffer.empty[AP]
  val fixedTemp = new com.badlogic.gdx.utils.Array[AP]()

  private def updateFalling(delta: Float) {
    {
      val continuedBuf = manual[PuzzleBuffer]
      val fallingBuf = manual[PuzzleBuffer]
      val fixedBuf = manual[PuzzleBuffer]

      {
        val width = falling.size
        var x = 0
        while (x < width) {
          val applied = falling(x)
          val height = applied.size
          var y = 0
          while (y < height) {
            val p = applied(y)
            p.vy() += gravity * delta
            if (p.updateFall(delta)) {
              finishedBuf += p
              p.isFalling() = false
            } else {
              continuedBuf(x) += p
            }
            y += 1
          }
          x += 1
        }
      }
      if (!finishedBuf.isEmpty) {
        {
          var x = 0
          while (x < ROW) {
            val row = fallingBuf(x)
            var y = 0
            val conRow = continuedBuf(x)
            val height = conRow.size
            while (y < height) {
              row += conRow(y)
              y += 1
            }
            x += 1
          }
          copy(fallingBuf)(falling)
        }
        {
          var x = 0
          while (x < ROW) {
            val row = fixed(x)
            val length = row.size
            val buf = fixedBuf(x)
            fixedTemp.clear()
            var y = 0
            while (y < length) {
              fixedTemp add row(y)
              y += 1
            }
            buf.clear()
            while (fixedTemp.size != 0) {
              buf += fixedTemp.pop()
            }
            x += 1
          }
        }
        {
          var i = 0
          val l = finishedBuf.length
          while (i < l) {
            val p = finishedBuf(i)
            fixedBuf(p.tx) += p
            i += 1
          }
        }
        copy(fixedBuf)(fixed)
        scanAndMark()
      }
      finishedBuf.clear()

      continuedBuf.free
      fallingBuf.free
      fixedBuf.free

    }
  }


  /**
   * container of the actual panel
   * why does this class have to be the inner class even while the
   * other classes are accessing this class's parameters?
   * It's because it has access to the fixed/falling buffers of the puzzle
   */
  class AP {
    /**
     * information that is directly connected to this instance
     * can be stored in here
     */
    var extra:AnyRef = null
    var debugState = 0
    //TODO make this poolable
    var value: T = null.asInstanceOf[T]
    val x = FloatVar(0f)
    val y = FloatVar(0f)
    val vx = FloatVar(0f)
    val vy = FloatVar(0f)
    var tx = 0
    var ty = 0
    val swipeAnimation = new ArrayBuffer[Task]() {
      override def +=(elem: Task): this.type = {
        val res = super.+=(elem)
        isSwiping() = !isEmpty
        res
      }

      override def -=(x: Task): this.type = {
        val res = super.-=(x)
        isSwiping() = !isEmpty
        res
      }

      override def clear(): Unit = {
        super.clear()
        isSwiping() = !isEmpty
      }
    }
    val isSwiping = Var(false)
    val isFalling = Var(false)
    val matchTimer = FloatVar(0f)
    def isMatching = matchTimer() > 0

    def checkContains(buf: IndexedSeq[IndexedSeq[AP]], tgt: AP): Boolean = {
      var x = 0
      val width = buf.size
      var result = false
      while (x < width && !result) {
        var y = 0
        val col = buf(x)
        val height = col.size
        while (y < height && !result) {
          val p = col(y)
          result = tgt eq p
          y += 1
        }
        x += 1
      }
      result
    }
    def updateFall(delta: Float): Boolean = {
      val nx = x() + vx() * delta
      var ny = y() + vy() * delta
      val row = future(tx)
      val finished: Boolean = if (ty > 0 && ty <= row.size) {
        val p = row(ty - 1)
        if (checkContains(fixed, p)) {
          debugState = 1
          (ny - ty) < 0f
        } else if (ny - p.y() < 1f) {
          debugState = 2
          //if above the next panel
          ny = p.y() + 1
          vy() = p.vy()
          //clear()
          false
        } else {
          debugState = 3
          (ny - ty) < 0f
        }
      } else {
        debugState = 3
        (ny - ty) < 0f
      }
      if (finished) {
        debugState = 0
        ny = ty
        clear() //こいつがmatchTimerまでリセットしていやがった
      }
      x() = nx
      y() = ny
      //println(y(),vy())
      finished
    }
    def updateMatch(delta: Float): Boolean = {
      matchTimer() -= delta
      matchTimer() < 0f
    }
    def clear() {
      vx() = 0
      vy() = 0
      isSwiping() = false
      isFalling() = false
      //matchTimer() = 0f
    }
    val canceler = (t: Task) => t.cancel()
    def reset() {
      extra = null
      debugState = 0
      value = null.asInstanceOf[T]
      x() = 0f
      y() = 0f
      vx() = 0f
      vy() = 0f
      tx = 0
      ty = 0
      swipeAnimation.foreach(canceler)
      swipeAnimation.clear()
      isSwiping() = false
      isFalling() = false
      matchTimer() = 0f
    }
    override def toString: String = value.toString
  }
}
object ActionPuzzle{
  type Panels[T] = IndexedSeq[T]
  type PanelBuffer[T] = ArrayBuffer[T]
  type PuzzleBuffer[T] = ArrayBuffer[PanelBuffer[T]]
  type PatternBuffer[T] = PuzzleBuffer[T]
  type Puzzle[T] = IndexedSeq[IndexedSeq[T]]
}