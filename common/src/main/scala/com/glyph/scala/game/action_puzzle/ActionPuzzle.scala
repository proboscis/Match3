package com.glyph.scala.game.action_puzzle

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.ui.{Skin, WidgetGroup}
import com.glyph.scala.lib.util.reactive.{Varying, Reactor, Var}
import scalaz._
import Scalaz._
import com.badlogic.gdx.math.{Interpolation, MathUtils}
import com.glyph.scala.lib.util.updatable.task._
import com.glyph.scala.game.action_puzzle.view.Paneled
import com.glyph.scala.lib.util.{HeapMeasure, Timing, reactive, Logging}
import com.glyph.scala.lib.util.updatable.reactive.Animator
import com.badlogic.gdx.graphics.{Color, Texture}
import com.glyph.scala.lib.libgdx.actor.{ExplosionFadeout, SpriteActor}
import com.glyph.scala.game.puzzle.view.match3.ColorTheme
import com.badlogic.gdx.graphics.g2d.Sprite
import scala.Some
import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.collection.mutable
import com.glyph.scala.lib.util.pool.{Pool, Pooler}
import com.glyph.scala.lib.util.pooling_task.PoolingTask
import com.glyph.scala.lib.util.updatable.reactive.Animator.IPAnimator
import com.glyph.scala.lib.util.pool.Pool._
import scala.Some
/**
 * @author glyph
 */
class ActionPuzzle extends Logging with Timing with HeapMeasure {
  //TODO マクロについて、ログ関数へ対応させる
  //TODO
  //TODO モードの実装とアップロードの準備

  /**
   * 時間差でマッチ
   * 時間で敵が攻撃してくる
   * 剣と敵のマッチ
   * パネルの効果設定
   */
  import GMatch3._
  import Animator._
  import Pool._
  import PoolingTask._

  val ROW = 6
  val COLUMN = 6
  val generator = new IndexedSeqGen[ArrayBuffer] {
    def convert[T](seq: Seq[T]) = ArrayBuffer.apply(seq: _*)
  }
  type PuzzleBuffer = ArrayBuffer[ArrayBuffer[AP]]
  implicit object PoolerPuzzle extends Pooler[PuzzleBuffer] {
    def newInstance = GMatch3.initialize[AP, ArrayBuffer](COLUMN)(generator)
    def reset(tgt: PuzzleBuffer) {
      var x = 0
      val l = tgt.length
      while (x < l) {
        tgt(x).clear()
        x += 1
      }
    }
  }

  //TODO tune the numbers
  implicit val animators = Pool[IPAnimator](1000)
  implicit val waiters = Pool[WaitAll](100)
  implicit val finishes = Pool[OnFinish](100)
  implicit val swipeAnimations = Pool[SwipeAnimation](() => new SwipeAnimation)(100)
  implicit val puzzlePool = Pool[PuzzleBuffer](100)

  val gravity = -10f
  val processor = new ParallelProcessor {}

  def initializer: Var[PuzzleBuffer] = Var(obtain[PuzzleBuffer])

  val seed: () => AP = () => new AP(MathUtils.random(0, 3))
  val fixed = obtain[PuzzleBuffer]
  val falling = obtain[PuzzleBuffer]
  val future = obtain[PuzzleBuffer]
  //callbacks
  var panelAdd = (panels: Seq[Seq[AP]]) => {}
  var panelRemove = (panels: Seq[AP]) => {}

  //util functions
  def scanAllDistinct2 = {//TODO こいつもバッファベースでやりたい
    val buf = obtain[PuzzleBuffer]
    GMatch3.fixedFuture(fixed,future,buf)
    val result = GMatch3.scanAll(buf)(ROW)(COLUMN) {
      (a, b) => {
        if (a != null && b != null && !a.isSwiping() && !b.isSwiping()) {
          a.n == b.n
        } else {
          false
        }
      }
    }.filter(_.size > 2).flatten
    buf.free
    result
  }

  def scanRemoveFill() {
    remove(scanAllDistinct2)
    fill()
  }

  def verified(x: Int)(y: Int)(nx: Int)(ny: Int) = y < fixed(x).size && ny < fixed(nx).size

  def pooledSwipe(x: Int, y: Int, nx: Int, ny: Int) {
    val anim = Pool.obtain[SwipeAnimation]

    if (verified(x)(y)(nx)(ny)) {

      val pa = future(x)(y)
      val pb = future(nx)(ny)
      var pTask: Task = null

      val task = anim.init(x, y, nx, ny, pa, pb, () => {
        pa.swipeAnimation -= pTask
        pb.swipeAnimation -= pTask

        if (verified(x)(y)(nx)(ny)) {
          GMatch3.swap(fixed, x, y, nx, ny)
          scanRemoveFill()
        }
        anim.free
      })
      pTask = task

      pa.swipeAnimation += pTask
      pb.swipeAnimation += pTask
      GMatch3.swap(future, x, y, nx, ny)
      processor.add(pTask)
    }
  }

  class SwipeAnimation {
    var ax, ay, bx, by: IPAnimator = null
    var waiter: WaitAll = null
    var onFin: OnFinish = null

    def init(x: Int, y: Int, nx: Int, ny: Int, pa: AP, pb: AP, callback: () => Unit): Task = {
      import Interpolation._
      ax = animators.obtain set pa.x to nx in 0.3f using exp10Out
      ay = animators.obtain set pa.y to ny in 0.3f using exp10Out
      bx = animators.obtain set pb.x to x in 0.3f using exp10Out
      by = animators.obtain set pb.y to y in 0.3f using exp10Out
      waiter = waiters.obtain
      waiter.add(ax)
      waiter.add(ay)
      waiter.add(bx)
      waiter.add(by)
      onFin = finishes.obtain
      onFin.setCallback(callback)
      onFin.setTask(waiter)
      onFin
    }

    def reset() {
      //TODO こいつをリストに収めておきたい><
      if (ax != null) ax.free
      if (ay != null) ay.free
      if (bx != null) bx.free
      if (by != null) by.free
      if (waiter != null) waiter.free
      if (onFin != null) onFin.free
      ax = null
      ay = null
      bx = null
      by = null
      waiter = null
      onFin = null
    }
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
    val filling = future createFillingPuzzle(seed, COLUMN) //no cost
    if (filling.exists(!_.isEmpty)) {
      //printTime("fill:update failling"){
      val nextFall: PuzzleBuffer = obtain[PuzzleBuffer]
      copy(falling)(nextFall)
      append(filling)(nextFall)
      //log("fill:setfalling")
      PoolerPuzzle.reset(falling)
      copy(nextFall)(falling)
      setFallingFlag()
      nextFall.free
      //falling() = falling() append filling // howmuch?
      //}
      //printTime("fill:update future"){
      val nextFuture: PuzzleBuffer = obtain[PuzzleBuffer]
      copy(fixed)(nextFuture)
      append(falling)(nextFuture)
      //log("fill:setFuture")
      PoolerPuzzle.reset(future)
      copy(nextFuture)(future)
      nextFuture.free
      //future() = fixed() append falling() //costs 1ms

      updateTargetPosition()

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

  def remove(panels: Seq[AP]) {
    //log("remove")
    if (!panels.isEmpty) {
      for {
        fixedTemp <- pool[PuzzleBuffer]
        fallingTemp <- pool[PuzzleBuffer]
        ffBuf <- pool[PuzzleBuffer]
      } {
        GMatch3.fixedFuture(fixed,future,ffBuf)
        GMatch3.remove(ffBuf)(fixedTemp)(fallingTemp)(panels)
        fallingTemp.foreach(_.foreach(cancelSwipingAnimation))
        append(falling)(fallingTemp)
        copy(fallingTemp)(falling)
        setFallingFlag()
        panelRemove(panels)
        copy(fixedTemp)(fixed)
      }
      for (futureTemp <- pool[PuzzleBuffer]) {
        copy(fixed)(futureTemp)
        append(falling)(futureTemp)
        // log("remove:setFuture")
        copy(futureTemp)(future)
      }
      /*
      fixed() = left //wait中にfixedを変更するとバグが発生するおそれがある。　要注意
      falling() = fallen append falling()
      future() = fixed() append falling()
      */
    }
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
  }

  def update(delta: Float) {
    updateFalling(delta)
    processor.update(delta)
  }

  //for optimization
  val finishedBuf = ListBuffer.empty[AP]
  val fixedTemp = mutable.Stack.empty[AP]

  def updateFalling(delta: Float) {
    for {
      continuedBuf <- pool[PuzzleBuffer]
      fallingBuf <- pool[PuzzleBuffer]
      fixedBuf <- pool[PuzzleBuffer]
    } {
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
            if (p.update(delta)) {
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
        scanRemoveFill()
      }
      finishedBuf.clear()
    }
  }

  class AP(val n: Int) extends Reactor {
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

    def matchTo(panel: AP): Boolean = n == panel.n

    def update(delta: Float): Boolean = {
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
        clear()
      }
      x() = nx
      y() = ny
      //println(y(),vy())
      finished
    }

    def clear() {
      vx() = 0
      vy() = 0
    }

    override def toString: String = n + ""
  }

}

class APView(puzzle: ActionPuzzle, assets: AssetManager) extends WidgetGroup with Paneled[Token] with Reactor with Logging {
  def row: Int = puzzle.ROW

  def column: Int = puzzle.COLUMN

  import Pool._

  implicit val tokenPool = Pool[Token](() => new Token(null, assets))(row * column)
  val skin = assets.get[Skin]("skin/default.json")
  val panelAdd = (added: Seq[Seq[ActionPuzzle#AP]]) => {
    for (row <- added; p <- row) {
      val token = obtain[Token]
      token.init(p)
      //TODO check for alignment
      token.reactVar(p.x)(x => token.setX(calcPanelX(x)))
      token.reactVar(p.y)(y => token.setY(calcPanelY(y)))
      token.setSize(panelW, panelH)
      token.setOrigin(panelW / 2, panelH / 2)
      tokens += token
      puzzleGroup.addActor(token)
    }
  }
  val panelRemove = (removed: Seq[ActionPuzzle#AP]) => {
    for (panel <- removed; token <- tokens.find(_.panel == panel)) {
      tokens -= token
      token.explode {
        token.remove()
        token.free
      }
    }
  }
}

class Token(var panel: ActionPuzzle#AP, assets: AssetManager)
  extends SpriteActor(new Sprite(assets.get[Texture]("data/dummy.png")))
  with Reactor
  with ExplosionFadeout {

  import reactive._

  def init(p: ActionPuzzle#AP) {
    panel = p
    import Token._
    val c = (colorMap.get(panel.n) | Var(Color.WHITE)) ~ panel.isSwiping ~ panel.isFalling map {
      case col ~ swiping ~ falling => (swiping | falling) ? col.cpy().mul(0.7f) | col
    }
    reactVar(c)(setColor)
  }

  def reset() {
    clearReaction()
    setScale(1)
  }
}

object Token {

  import ColorTheme._

  val colorMap: Int Map Varying[Color] = Map(0 -> ColorTheme.fire, 1 -> thunder, 2 -> water, 3 -> life)
}