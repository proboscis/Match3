package com.glyph.scala.game.action_puzzle

import com.glyph.scala.lib.util.reactive.{Reactor, Var}
import com.badlogic.gdx.math.Interpolation
import com.glyph.scala.lib.util.updatable.task._
import com.glyph.scala.lib.util.{HeapMeasure, Timing, Logging}
import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.collection.mutable
import com.glyph.scala.lib.util.pool.{Pool, Pooling}
import com.glyph.scala.lib.util.animator.Animator
import scala.Some
import com.glyph.scala.game.Glyphs
import Glyphs._


/**
 * @author glyph
 */
class ActionPuzzle[T](val ROW: Int, val COLUMN: Int, seed: () => T, filterFunction: (T, T) => Boolean)
  extends Logging
  with Timing
  with HeapMeasure {
  //TODO where is allocation?????
  //TODO システムを決めなければ、素材を作ることができない。
  //TODO
  //TODO マクロについて、ログ関数へ対応させる
  //TODO
  //TODO モードの実装とアップロードの準備\
  //TODO スコアとゲージの実装
  //TODO パズルの表示位置がずれる問題を修正(再出現時にずれる)

  /**
   * what should i do next?
   * scoring
   * dissappearing particles
   *
   */

  //TODO Particleの実装
  /**
   * 時間差でマッチ
   * 時間で敵が攻撃してくる
   * 剣と敵のマッチ
   * パネルの効果設定
   */

  import GMatch3._
  import Animator._
  import Pool._

  val matcher = new Matcher[AP]
  val MATCHING_TIME = 1f
  val generator = new IndexedSeqGen[ArrayBuffer] {
    def convert[T](seq: Seq[T]) = ArrayBuffer.apply(seq: _*)
  }
  type PuzzleBuffer = ArrayBuffer[ArrayBuffer[AP]]

  implicit object PoolingPuzzle extends Pooling[PuzzleBuffer] {
    def newInstance = GMatch3.initialize[AP, ArrayBuffer](COLUMN)(generator)
    val cleaner = (buf:ArrayBuffer[AP]) => buf.clear()
    def reset(tgt: PuzzleBuffer)= tgt foreach cleaner
  }

  val gravity = -10f
  val processor = new ParallelProcessor {}

  def initializer: Var[PuzzleBuffer] = Var(manual[PuzzleBuffer])

  implicit object PoolingAP extends Pooling[AP] {
    def newInstance: ActionPuzzle.this.type#AP = new AP
    def reset(tgt: ActionPuzzle.this.type#AP): Unit = tgt.reset()
  }

  val APSeed: () => AP = () => {
    val np = manual[AP]
    np.value = seed()
    np
  }
  val fixed = manual[PuzzleBuffer]
  val falling = manual[PuzzleBuffer]
  val future = manual[PuzzleBuffer]
  //callbacks
  var panelAdd = (panels: Seq[Seq[AP]]) => {}
  var panelRemove = (panels: Seq[AP]) => {}

  val APFilter = (a: AP, b: AP) => {
    if (a != null && b != null && !a.isSwiping() && !b.isSwiping()) {
      filterFunction(a.value, b.value)
    } else {
      false
    }
  }
  def scanAndMark() {
    val buf = manual[PuzzleBuffer]
    GMatch3.fixedFuture(fixed, future, buf)
    matcher.scanAll(buf, ROW, COLUMN, APFilter, matches => {
      if (matches.size > 2) {
        var chmp = MATCHING_TIME
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
    })
    buf.free
  }

  def verified(x: Int)(y: Int)(nx: Int)(ny: Int) =
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
      seqA.add(taskA)
      seqA.add(Do {
        pa.swipeAnimation -= taskA
      })
      val taskB = swipeAnimation(x, y, pb)
      val seqB = auto[Sequence]
      seqB.add(taskB)
      seqB.add(Do {
        pb.swipeAnimation -= taskB
      })
      pa.swipeAnimation += taskA
      pb.swipeAnimation += taskB
      //TODO swap after a or b is finished
      // you have to callback when either of these are unexpectedly stopped
      par.add(seqA)
      par.add(seqB)
      seq.add(par)
      seq.add(Do {
        if (verified(x)(y)(nx)(ny)) {
          GMatch3.swap(fixed, x, y, nx, ny)
          scanAndMark()
        }
      })
      processor.add(seq)
      GMatch3.swap(future, x, y, nx, ny)
    }
  }

  def swipeAnimation(nx: Int, ny: Int, pa: AP): Task = {
    import Interpolation._
    val apx = auto[IPAnimator]
    val apy = auto[IPAnimator]
    apx set pa.x to nx in 0.3f using exp10Out
    apy set pa.y to ny in 0.3f using exp10Out
    val par = auto[Parallel]
    par.add(apx)
    par.add(apy)
    par
  }

  def setFallingFlag() {
    var x = 0
    val fallen = falling
    val width = fallen.length
    while (x < width) {
      val row = fallen(x)
      val height = row.length
      var y = 0
      while (y < height) {
        val p = row(y)
        p.isFalling() = true
        y += 1
      }
      x += 1
    }
  }

  def fill() {
    //log("fill")
    val filling = future createFillingPuzzle(APSeed, COLUMN) //no cost
    if (filling.exists(!_.isEmpty)) {
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

      {
        var x = 0
        val width = filling.length
        while (x < width) {
          val row = filling(x)
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
      panelAdd(filling)
    }
  }

  def cancelSwipingAnimation(panel: AP) {
    //TODO set a panel to appropriate position
    //TODO free the canceled animation
    panel.swipeAnimation.clear()
  }

  val panelResetter = (p: AP) => p.free

  def removeFillUpdateTargetPosition(panels: Seq[AP], fallings: Seq[AP]) {
    //log("remove")
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
        fallingTemp.foreach(_.foreach(cancelSwipingAnimation))
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
        p.tx() = x
        p.ty() = y
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

  def updateMatches(delta: Float) {
    //fallingについてもタイマーを考慮するようにしたい
    {
      var x = 0
      val width = fixed.size
      while (x < width) {
        val row = fixed(x)
        val height = row.size
        var y = 0
        while (y < height) {
          val panel = row(y)
          if (panel.isMatching()) {
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
          if (p.isMatching()) {
            if (p.updateMatch(delta)) fallingRemoveBuf += p
          }
          y += 1
        }
        x += 1
      }
    }
    if (!matchRemoveBuf.isEmpty || !fallingRemoveBuf.isEmpty) {
      removeFillUpdateTargetPosition(matchRemoveBuf, fallingRemoveBuf) //TODO this is causing memory allocation!
    }
    fallingRemoveBuf.clear()
    matchRemoveBuf.clear()
  }

  //for optimization
  val finishedBuf = ListBuffer.empty[AP]
  val fixedTemp = mutable.Stack.empty[AP]

  def updateFalling(delta: Float) {
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
              fixedTemp.push(row(y))
              y += 1
            }
            buf.clear()
            while (!fixedTemp.isEmpty) {
              buf += fixedTemp.pop()
            }
            x += 1
          }
        }
        for (p <- finishedBuf) {
          fixedBuf(p.tx()) += p
          //fixed() = fixed().updated(p.tx(), fixed()(p.tx()) :+ p)
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
  class AP extends Reactor {
    //TODO make this poolable
    var value: T = null.asInstanceOf[T]
    val x = Var(0f)
    val y = Var(0f)
    val vx = Var(0f)
    val vy = Var(0f)
    val tx = Var(0)
    val ty = Var(0)
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
    val matchTimer = Var(0f)
    val isMatching = matchTimer map (_ > 0)

    def updateFall(delta: Float): Boolean = {
      val nx = x() + vx() * delta
      var ny = y() + vy() * delta
      val next = PartialFunction.condOpt(ty() - 1)(future(tx()))
      val finished = next match {
        case Some(p) if fixed.exists(_.contains(p)) => (ny - ty()) < 0f
        case Some(p) if ny - p.y() < 1f => {
          //if above the next panel
          ny = p.y() + 1
          vy() = p.vy()
          //clear()
          false
        }
        case _ => (ny - ty()) < 0f
      }
      if (finished) {
        ny = ty()
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
      value = null.asInstanceOf[T]
      x() = 0f
      y() = 0f
      vx() = 0f
      vy() = 0f
      tx() = 0
      ty() = 0
      swipeAnimation.foreach(canceler)
      swipeAnimation.clear()
      isSwiping() = false
      isFalling() = false
      matchTimer() = 0f
    }
    override def toString: String = value.toString
  }
}