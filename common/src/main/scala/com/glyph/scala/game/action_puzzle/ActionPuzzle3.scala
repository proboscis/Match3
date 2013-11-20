package com.glyph.scala.game.action_puzzle

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.ui.{Skin, WidgetGroup}
import com.glyph.scala.lib.util.reactive.{Reactor, Var}
import scalaz._
import Scalaz._
import com.badlogic.gdx.math.{Interpolation, MathUtils}
import com.glyph.scala.lib.util.updatable.task._
import com.glyph.scala.game.action_puzzle.view.Paneled
import com.glyph.scala.lib.util.{Timing, reactive, Logging}
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

/**
 * @author glyph
 */
//@hello
class ActionPuzzle3 extends Reactor with Logging with Timing {
  //TODO マクロについて、ログ関数へ対応させる
  import GMatch3._
  import Animator._
  import Pool._
  import PoolingTask._
  val ROW = 8
  val COLUMN = 8
  val generator = new IndexedSeqGen[ArrayBuffer] {
    def convert[T](seq: Seq[T])= ArrayBuffer.apply(seq: _*)
  }
  implicit object PoolingSA extends Pooling[SwipeAnimation] {
    def newInstance = new SwipeAnimation
    def reset(tgt: SwipeAnimation): Unit = tgt.resetAll()
  }
  type PuzzleBuffer = ArrayBuffer[ArrayBuffer[AP]]
  implicit object PoolingPuzzle extends Pooling[PuzzleBuffer]{
    def newInstance = GMatch3.initialize[AP,ArrayBuffer](COLUMN)(generator)
    def reset(tgt:PuzzleBuffer){
      var x = 0
      val l = tgt.length
      while(x < l){
        tgt(x).clear()
        x += 1
      }
    }
  }
  implicit val animators = Pool[IPAnimator](1000)
  implicit val waiters = Pool[WaitAll](100)
  implicit val finishes = Pool[OnFinish](100)
  implicit val swipeAnimations = Pool[SwipeAnimation](100)
  implicit val puzzlePool = Pool[PuzzleBuffer](100)

  val gravity = -10f
  val processor = new ParallelProcessor {}
  def initializer: Var[PuzzleBuffer] = Var(Pool.obtain[PuzzleBuffer])
  def seed: () => AP = () => new AP(MathUtils.random(0, 3))
  val fixed = initializer
  val falling = initializer
  val swiping: Var[AP Map Seq[Task]] = Var(Map.empty.withDefaultValue(Nil))
  val future = initializer
  val futureIndices = future map GMatch3.toIndexMap
  val fallingFlagMap = falling map GMatch3.toContainsMap
  //callbacks
  var panelAdd = (panels: Seq[(AP, Int, Int)]) => {}
  var panelRemove = (panels: Seq[AP]) => {}
  //util functions
  def scanAllDistinct2 = {
    val cpy = swiping()
    GMatch3.scanAll(fixedFuture)(ROW)(COLUMN) {
      (a, b) => {
        if (a != null && b != null && cpy(a).isEmpty && cpy(b).isEmpty) {
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
  def pooledSwipe(x: Int, y: Int, nx: Int, ny: Int) = {
    //("animator"+animators)::("swipe"+swipeAnimations)::Nil foreach println
    //TODO make this swipe animation poolable!
    //type class AutoRecycle!
    val anim = swipeAnimations.obtain
    def verified = y < fixed()(x).size && ny < fixed()(nx).size
    if (verified) {
      val pa = future()(x)(y)
      val pb = future()(nx)(ny)
      var pTask:Task = null
      val task = anim.init(x, y, nx, ny,pa,pb, () => {
        swiping() ++= (pa -> swiping()(pa).filterNot(_ == pTask)) :: (pb -> swiping()(pb).filterNot(_ == pTask)) :: Nil
        swiping() = swiping().filterNot(_._2.isEmpty)
        if (verified) {
          val prev = fixed()
          val next = Pool.obtain[PuzzleBuffer]
          GMatch3.swap(prev)(GMatch3.copy(prev)(next))(x,y,nx,ny)
          fixed ()= next
          prev.free
          //fixed() = fixed().swap(x, y, nx, ny)
          scanRemoveFill()
        }
        anim.free
        //swipeAnimations.reset(anim)
      })
      pTask = task
      swiping() ++= (pa -> (swiping()(pa) :+ task)) :: (pb -> (swiping()(pb) :+ task)) :: Nil
      val prev = future()
      future() = GMatch3.swap(fixed())(GMatch3.copy(future())(Pool.obtain[PuzzleBuffer]))(x,y,nx,ny)
      prev.free
      //future() = future().swap(x, y, nx, ny)
      processor.add(task)
    }
  }
  class SwipeAnimation {
    var ax,ay,bx,by :IPAnimator = null
    var waiter:WaitAll = null
    var onFin:OnFinish = null
    def init(x: Int, y: Int, nx: Int, ny: Int,pa:AP,pb:AP, callback: () => Unit): Task = {
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
      ax.free
      ay.free
      bx.free
      by.free
      waiter.free
      onFin.free
      ax = null
      ay = null
      bx = null
      by = null
      waiter = null
      onFin = null
    }
  }
  def fill() {
    val filling = future() createFillingPuzzle(seed, COLUMN) //no cost
    if (filling.exists(!_.isEmpty)) {
      //printTime("fill:update failling"){
      val prevFall = falling()
      falling() =append(prevFall)(copy(filling)(Pool.obtain[PuzzleBuffer]))
      prevFall.free
      //falling() = falling() append filling // howmuch?
      //}
      //printTime("fill:update future"){
      val prevFuture = future()
      future() = append(prevFuture)(copy(falling())(Pool.obtain[PuzzleBuffer]))
      //future() = fixed() append falling() //costs 1ms
      prevFuture.free
      //}
      val fillingMap = filling.toIndexMap // costs 1ms
      val indexed = filling.flatten.map {
          p => val (x, y) = fillingMap(p); (p, x, y)
        }
      val futureMap = futureIndices()
      val futureIndexed = filling.flatten.map {
        p => val (x, y) = futureMap(p); (p, x, y)
      }
      for ((p, x, y) <- indexed) {
        p.x() = x
        p.y() = COLUMN + y
      }
      panelAdd(futureIndexed)
    }
    updateTargetPosition()
  }
  def cancelSwipingAnimation(panel: AP) {
    //TODO set a panel to appropriate position
    for {
      tasks <- swiping().get(panel)
      task <- tasks
    } {
      swiping() += (panel -> swiping()(panel).filterNot(_ == task))
      //processor.removeTask(task)
    }
  }
  def remove(panels: Seq[AP]) {
    if (!panels.isEmpty) {
      val (left, fallen) = fixedFuture.remove(panels)
      fallen.foreach(_.foreach(cancelSwipingAnimation))
      panelRemove(panels)
      fixed() = left //wait中にfixedを変更するとバグが発生するおそれがある。　要注意
      falling() = fallen append falling()
      future() = fixed() append falling()
    }
    updateTargetPosition()
  }
  def updateTargetPosition() {
    //this is a bit heavy opreation....
    val indices = futureIndices()
    for {
      row <- falling()
      p <- row
    } {
      val (tx, ty) = indices(p) //future().indexOfPanelUnhandled(p)
      p.tx() = tx
      p.ty() = ty
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
  def newPuzzleBuffer: ArrayBuffer[ArrayBuffer[AP]] = ArrayBuffer((0 until ROW).map(_ => ArrayBuffer.empty[AP]): _*)
  val finishedBuf = ListBuffer.empty[AP]
  val continuedBuf = newPuzzleBuffer
  val fallingBuffer = newPuzzleBuffer
  val fixedBuf = newPuzzleBuffer
  val fixedTemp = mutable.Stack.empty[AP]
  def updateFalling(delta: Float) {
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
    /*
    val (finished, continued) = falling().unzip {
      _.partition(p => {
        p.vy() += gravity * delta
        p.update(delta)
      })
    }
    */
    //println("continued:"+continued.text)
    if (!finishedBuf.isEmpty) {
      //println("finished:" + finished.text)
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
        falling() = fallingBuffer
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
      fixed() = fixedBuf
      scanRemoveFill()
    }
    var i = 0
    while (i < ROW) {
      continuedBuf(i).clear()
      i += 1
    }
    finishedBuf.clear()
  }
  class AP(val n: Int) extends Reactor {
    val x = Var(0f)
    val y = Var(0f)
    val vx = Var(0f)
    val vy = Var(0f)
    val tx = Var(0)
    val ty = Var(0)
    lazy val isSwiping = swiping.map(!_(this).isEmpty)
    lazy val isFalling = fallingFlagMap.map(_(this))
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
  val skin = assets.get[Skin]("skin/default.json")
  val panelAdd = (added: Seq[(ActionPuzzle3#AP, Int, Int)]) => {
    for ((p, x, y) <- added) {
      val token = new Token(p, assets)
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
      }
    }
  }
}

class Token(val panel: ActionPuzzle3#AP, assets: AssetManager)
  extends SpriteActor(new Sprite(assets.get[Texture]("data/dummy.png")))
  with Reactor
  with ExplosionFadeout {
  import ColorTheme._
  import reactive._
  val colorMap: Int Map Varying[Color] = Map(0 -> ColorTheme.fire, 1 -> thunder, 2 -> water, 3 -> life)
  val c = (colorMap.get(panel.n) | Var(Color.WHITE)) ~ panel.isSwiping ~ panel.isFalling map {
    case col ~ swiping ~ falling => (swiping | falling) ? col.cpy().mul(0.7f) | col
  }
  reactVar(c)(setColor)
  //debug()
}

