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
import com.glyph.scala.lib.util.pool.{Pool, Pooling}
import com.glyph.scala.lib.util.pooling_task.PoolingTask
import com.glyph.scala.lib.util.updatable.reactive.Animator.IPAnimator

/**
 * @author glyph
 */
//@hello
class ActionPuzzle3 extends Reactor with Logging with Timing with HeapMeasure {
  //TODO マクロについて、ログ関数へ対応させる

  import GMatch3._
  import Animator._
  import Pool._
  import PoolingTask._

  val ROW = 8
  val COLUMN = 8
  val generator = new IndexedSeqGen[ArrayBuffer] {
    def convert[T](seq: Seq[T]) = ArrayBuffer.apply(seq: _*)
  }

  implicit object PoolingSA extends Pooling[SwipeAnimation] {
    def newInstance = new SwipeAnimation

    def reset(tgt: SwipeAnimation): Unit = tgt.resetAll()
  }

  type PuzzleBuffer = ArrayBuffer[ArrayBuffer[AP]]

  implicit object PoolingPuzzle extends Pooling[PuzzleBuffer] {
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
  implicit val swipeAnimations = Pool[SwipeAnimation](100)
  implicit val puzzlePool = Pool[PuzzleBuffer](100)

  val gravity = -10f
  val processor = new ParallelProcessor {}

  def initializer: Var[PuzzleBuffer] = Var(Pool.obtain[PuzzleBuffer])

  val seed: () => AP = () => new AP(MathUtils.random(0, 2))
  val fixed = initializer
  val falling = initializer
  val future = initializer
  //callbacks
  var panelAdd = (panels: Seq[Seq[AP]]) => {}
  var panelRemove = (panels: Seq[AP]) => {}

  //util functions
  def scanAllDistinct2 = {
    GMatch3.scanAll(fixedFuture)(ROW)(COLUMN) {
      (a, b) => {
        if (a != null && b != null && !a.isSwiping() && !b.isSwiping()) {
          a.n == b.n
        } else {
          false
        }
      }
    }.filter(_.size > 2).flatten
  }

  def fixedFuture = {
    //こいｔも遅い
    fixed().zipWithIndex.map {
      case (row, x) => row.zipWithIndex.map {
        case (p, y) => future()(x)(y)
      }
    }
  }

  def scanRemoveFill() {
    //TODO removeとfillの高速化
    remove(scanAllDistinct2)
    fill()
  }

  def verified(x: Int)(y: Int)(nx: Int)(ny: Int) = y < fixed()(x).size && ny < fixed()(nx).size

  def pooledSwipe(x: Int, y: Int, nx: Int, ny: Int){
    val anim = Pool.obtain[SwipeAnimation]

    if (verified(x)(y)(nx)(ny)) {

      val pa = future()(x)(y)
      val pb = future()(nx)(ny)
      var pTask: Task = null

      val task = anim.init(x, y, nx, ny, pa, pb, () => {

        pa.swipeAnimation() = pa.swipeAnimation().filterNot(_ == pTask)//TODO this may better be made of ArrayBuffer
        pb.swipeAnimation() = pb.swipeAnimation().filterNot(_ == pTask)


        if (verified(x)(y)(nx)(ny)) {
          val prev = fixed()
          val next = Pool.obtain[PuzzleBuffer]
          GMatch3.copy(prev)(next)
          GMatch3.swap(prev)(next)(x, y, nx, ny)
          fixed() = next
          prev.free
          scanRemoveFill()
        }
        anim.free
      })
      pTask = task

      pa.swipeAnimation() ::= pTask
      pb.swipeAnimation() ::= pTask

      val prev = future()
      val next = Pool.obtain[PuzzleBuffer]
      GMatch3.copy(prev)(next)
      GMatch3.swap(prev)(next)(x, y, nx, ny)
      future() = next
      prev.free
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

    def resetAll() {
      //TODO こいつをリストに収めておきたい><
      if(ax != null) ax.free
      if(ay != null) ay.free
      if(bx != null) bx.free
      if(by != null) by.free
      if(waiter!= null) waiter.free
      if(onFin != null) onFin.free
      ax = null
      ay = null
      bx = null
      by = null
      waiter = null
      onFin = null
    }
  }

  def fill() {
    //log("fill")
    val filling = future() createFillingPuzzle(seed, COLUMN) //no cost
    if (filling.exists(!_.isEmpty)) {
      //printTime("fill:update failling"){
      val prevFall = falling()
      val nextFall: PuzzleBuffer = Pool.obtain[PuzzleBuffer]
      copy(prevFall)(nextFall)
      append(filling)(nextFall)
      //log("fill:setfalling")
      falling() = nextFall
      prevFall.free
      //falling() = falling() append filling // howmuch?
      //}
      //printTime("fill:update future"){
      val prevFuture = future()
      val nextFuture: PuzzleBuffer = Pool.obtain[PuzzleBuffer]
      copy(fixed())(nextFuture)
      append(falling())(nextFuture)
      //log("fill:setFuture")
      future() = nextFuture
      prevFuture.free
      //future() = fixed() append falling() //costs 1ms

      updateTargetPosition()

      {
        var x = 0
        val width = filling.length
        while ( x < width){
          val row = filling(x)
          val height = row.length
          var y = 0
          while(y < height){
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
    panel.swipeAnimation() = Nil
  }

  def remove(panels: Seq[AP]) {
    //log("remove")
    if (!panels.isEmpty) {
      val fixedPrev = fixed()
      val fixedNext: PuzzleBuffer = Pool.obtain[PuzzleBuffer]
      val fallingPrev = falling()
      val fallingNext: PuzzleBuffer = Pool.obtain[PuzzleBuffer]
      GMatch3.remove(fixedFuture)(fixedNext)(fallingNext)(panels)
      fallingNext.foreach(_.foreach(cancelSwipingAnimation))
      append(fallingPrev)(fallingNext)
      panelRemove(panels)
      //log("remove:setfixed")
      fixed() = fixedNext
      fixedPrev.free
      //log("remove:setFAlling")
      falling() = fallingNext
      fallingPrev.free
      val futurePrev = future()
      val futureNext: PuzzleBuffer = Pool.obtain[PuzzleBuffer]
      copy(fixed())(futureNext)
      append(falling())(futureNext)
      // log("remove:setFuture")
      future() = futureNext
      futurePrev.free
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
    val ft = future()
    val width = ft.length
    while(x < width){
      val row = ft(x)
      val height = row.length
      var y = 0
      while( y < height){
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
    val continuedBuf = Pool.obtain[PuzzleBuffer]
    val fallingBuffer = Pool.obtain[PuzzleBuffer]
    val fixedBuf = Pool.obtain[PuzzleBuffer]

    {
      val fallingCpy = falling()
      val width = fallingCpy.size
      var x = 0
      while (x < width) {
        val applied = fallingCpy(x)
        val height = applied.size
        var y = 0
        while (y < height) {
          val p = applied(y)
          p.vy() += gravity * delta
          if (p.update(delta)) {
            finishedBuf += p
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
          val row = fallingBuffer(x)
          row.clear()
          var y = 0
          val conRow = continuedBuf(x)
          val height = conRow.size
          while (y < height) {
            row += conRow(y)
            y += 1
          }
          x += 1
        }
        val prev = falling()
        falling() = fallingBuffer
        prev.free
      }
      //TODO make this mutable and fast!
      {
        var x = 0
        while (x < ROW) {
          val row = fixed()(x)
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
      val prev = fixed()
      fixed() = fixedBuf
      prev.free
      scanRemoveFill()
    } else {
      fixedBuf.free
      fallingBuffer.free
    }
    continuedBuf.free
    finishedBuf.clear()
  }

  class AP(val n: Int) extends Reactor {
    val x = Var(0f)
    val y = Var(0f)
    val vx = Var(0f)
    val vy = Var(0f)
    val tx = Var(0)
    val ty = Var(0)
    val swipeAnimation = Var[List[Task]](Nil)
    val isSwiping = swipeAnimation map (!_.isEmpty)
    import reactive._

    def matchTo(panel: AP): Boolean = n == panel.n

    def update(delta: Float): Boolean = {
      val nx = x() + vx() * delta
      var ny = y() + vy() * delta
      val next = PartialFunction.condOpt(ty() - 1)(future()(tx()))
      val finished = next match {
        case Some(p) if fixed().exists(_.contains(p)) => (ny - ty()) < 0f
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

class APView(puzzle: ActionPuzzle3, assets: AssetManager) extends WidgetGroup with Paneled[Token] with Reactor with Logging {
  def row: Int = puzzle.ROW

  def column: Int = puzzle.COLUMN

  import Pool._

  implicit object PoolingToken extends Pooling[Token] {
    def newInstance: Token = new Token(null, assets)

    def reset(tgt: Token): Unit = tgt.reset()
  }

  implicit val tokenPool = Pool[Token](row * column)
  val skin = assets.get[Skin]("skin/default.json")
  val panelAdd = (added: Seq[Seq[ActionPuzzle3#AP]]) => {
    for (row <- added;p <- row) {
      val token = Pool.obtain[Token]
      token.init(p)
      token.reactVar(p.x)(calcPanelX(_) |> token.setX)
      token.reactVar(p.y)(calcPanelY(_) |> token.setY)
      token.setSize(panelW, panelH)
      token.setOrigin(panelW / 2, panelH / 2)
      tokens += token
      puzzleGroup.addActor(token)
    }
  }
  val panelRemove = (removed: Seq[ActionPuzzle3#AP]) => {
    for (panel <- removed; token <- tokens.find(_.panel == panel)) {
      tokens -= token
      token.explode {
        token.remove()
        token.free
      }
    }
  }
}

class Token(var panel: ActionPuzzle3#AP, assets: AssetManager)
  extends SpriteActor(new Sprite(assets.get[Texture]("data/dummy.png")))
  with Reactor
  with ExplosionFadeout {

  import reactive._

  def init(p: ActionPuzzle3#AP) {
    panel = p
    import Token._
    val c = (colorMap.get(panel.n) | Var(Color.WHITE)) ~ panel.isSwiping map {
      case col ~ swiping => swiping ? col.cpy().mul(0.7f) | col
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