package com.glyph._scala.game.action_puzzle

import com.glyph._scala.lib.util.reactive.{FloatVar, Var}
import com.badlogic.gdx.math.Interpolation
import com.glyph._scala.lib.util.updatable.task._
import com.glyph._scala.lib.util.{HeapMeasure, Timing, Logging}
import scala.collection.mutable.ArrayBuffer
import com.glyph._scala.lib.util.pool.{Pool, Pooling}
import com.glyph._scala.game.Glyphs
import Glyphs._
trait Matcher[-T]{
  def patternMatch(puzzle:IndexedSeq[IndexedSeq[T]],row:Int,column:Int)
}

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
  //TODO reactiveのautoBoxing
  log("new ActionPuzzle")
  //NOTE 初回起動時に中断し再度開始するとfalling状態のパネルが戻らなくなり、パズルを再生成しても改善されない問題=>
  //Poolingを判別するタグがインナークラス同士で共有されてしまい、違うパズルで同じfutureを参照していたために発生した

  //TODO Particleの実装
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
  var panelAdd = (panels: IndexedSeq[IndexedSeq[AP]]) => {}
  /**
   * invoked when the panels are removed
   */
  var panelRemove = (panels: IndexedSeq[AP]) => {}
  /**
   * add matcher to be invoked when the scanning should be invoked
   */
  var matchers = ArrayBuffer[Matcher[AP]]()

  import GMatch3._

  type PuzzleBuffer = ArrayBuffer[ArrayBuffer[AP]]

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
  private val matcher = new LineMatcher[AP]
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

  private val APFilter = (a: AP, b: AP) => {
    if (a != null && b != null && !a.isSwiping() && !b.isSwiping()) {
      filterFunction(a.value, b.value)
    } else {
      -1
    }
  }

  private val arrayBufferResetter = (buf: ArrayBuffer[AP]) => buf.free
  private val matchBuffer = ArrayBuffer[ArrayBuffer[AP]]()
  private val timerUpdater = (matches: Seq[AP]) => {
    val buf = manual[ArrayBuffer[AP]]
    buf ++= matches
    matchBuffer += buf
    if (matches.size > 2) {
      var chmp = initialMatchingTime()
      for (p <- matches) {
        val t = p.matchTimer()
        if (t > 0 && t < chmp) chmp = t
      }
      val minimum = chmp
      matches foreach {
        p =>
          if (p.matchTimer() > 0f && minimum == 0f) throw new RuntimeException("what!!???")
          p.matchTimer() = minimum
      }
    }
  }

  //this is called when the panels is swiped or any panel finished falling
  //should be called wheneber the fixed buffer changes.
  private def scanAndMark() {
    val buf = manual[PuzzleBuffer]
    GMatch3.fixedFuture(fixed, future, buf)
    matcher.scanAll(buf, ROW, COLUMN, APFilter, timerUpdater)

    {//call all the matchers
      var i = 0
      val size = matchers.size
      while(i < size){
        matchers(i).patternMatch(buf,ROW,COLUMN)
        i+=1
      }
    }

    matchBuffer foreach arrayBufferResetter
    matchBuffer.clear()
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
        panelRemove(panels)
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