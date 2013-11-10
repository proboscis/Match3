package com.glyph.scala.game.action_puzzle

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.ui.{Skin, WidgetGroup}
import com.glyph.scala.lib.util.reactive.{Varying, Reactor, Var}
import scalaz._
import Scalaz._
import com.badlogic.gdx.math.{Interpolation, MathUtils}
import com.glyph.scala.lib.util.updatable.task._
import com.glyph.scala.game.action_puzzle.view.Paneled
import com.glyph.scala.lib.util.{reactive, Logging}
import com.glyph.scala.lib.util.updatable.reactive.Animator
import com.badlogic.gdx.graphics.{Color, Texture}
import com.glyph.scala.lib.libgdx.actor.{ExplosionFadeout, SpriteActor}
import com.glyph.scala.game.puzzle.view.match3.ColorTheme
import com.badlogic.gdx.graphics.g2d.Sprite
import scala.Some

/**
 * @author glyph
 */
class ActionPuzzle3 extends Reactor with Logging{
  //TODO マクロでコンパイル時に実行時間計測プログラムを追加する。
  import GMatch3._
  //TODO it's time to make this fast!
  val ROW = 8
  val COLUMN = 8
  val gravity = -10f
  val processor = new ParallelProcessor {}
  def initializer: Var[Puzzle[AP]] = Var(GMatch3.initialize(COLUMN))
  def seed: () => AP = () => MathUtils.random(0, 3) |> (new AP(_))
  val fixed = initializer
  val falling = initializer
  val swiping: Var[AP Map Seq[Task]] = Var(Map.empty)
  val future = initializer
  //callbacks
  var panelAdd = (panels: Seq[(AP, Int, Int)]) => {}
  var panelRemove = (panels: Seq[AP]) => {}

  //util functions
  def scanAll = scanAllWithException(fixedFuture)(3)(swiping().get(_) | Nil |> (!_.isEmpty))
  def scanAllDistinct = scanAll.flatten.map(_._1).distinct
  def fixedFuture = {
    fixed().zipWithIndex.map{
      case (row,x)=> row.zipWithIndex.map{
        case (p,y) => future()(x)(y)
      }
    }
  }
  def scanRemoveFill() {
    //println(scanAll)
    remove(scanAllDistinct)
    fill()
  }
  def swipe(x: Int, y: Int, nx: Int, ny: Int) = {
    try {
      def verified = y < fixed()(x).size  && ny < fixed()(nx).size
      if (verified) {
        val pa = future()(x)(y)
        val pb = future()(nx)(ny)
        import Animator._
        import Interpolation._
        var pTask:Task = null
        val task = (((pa.x, nx) ::(pa.y, ny) ::(pb.x, x) ::(pb.y, y) :: Nil map {
          case (v, tgt) => interpolate(v) to tgt in 0.3f using exp10Out
        }) |> (WaitAll(_: _*))) :: Do {
          swiping() ++= (pa -> (swiping().get(pa).map(_.filterNot(_ == pTask))|Nil)) :: (pb -> (swiping().get(pb).map(_.filterNot(_==pTask))|Nil)) :: Nil
          swiping() = swiping().filterNot(_._2.isEmpty)
          if (verified) {
            fixed() = fixed().swap(x, y, nx, ny)
            scanRemoveFill()
          }
        } :: Nil |> (Sequence(_: _*))
        pTask = task
        swiping() ++= (pa -> ((swiping().get(pa) | Nil) :+ task)) :: (pb -> ((swiping().get(pb) | Nil) :+ task)) :: Nil
        future() = future().swap(x, y, nx, ny)
        processor.add(task)
      }
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }

  def fill() {
    val filling = future() createFillingPuzzle(seed, COLUMN)
    if (filling.exists(!_.isEmpty)) {
      falling() = falling() append filling
      future() = fixed() append falling()
      val indexed = filling.flatten.map {
        p => val (x, y) = filling.indexOfPanelUnhandled(p); (p, x, y)
      }
      val futureIndexed = filling.flatten.map {
        p => val (x, y) = future().indexOfPanelUnhandled(p); (p, x, y)
      }
      for ((p, x, y) <- indexed) {
        p.x() = x
        p.y() = COLUMN + y
      }
      futureIndexed |> panelAdd
    }
    updateTargetPosition()
  }
  def cancelSwipingAnimation(panel:AP){
    //TODO set a panel to appropriate position
    for{
      tasks <- swiping().get(panel)
      task <- tasks
    }{
      log("canceled!"+panel)
      swiping() += (panel->(swiping().get(panel).map(_.filterNot(_==task))|Nil))
      //TODO remove task from the map.
      //processor.removeTask(task)
    }
  }

  def remove(panels: Seq[AP]) {
    if (!panels.isEmpty) {
      val (left, fallen) = fixedFuture.remove(panels)
      fallen.foreach(_.foreach(cancelSwipingAnimation))
      panelRemove(panels)
      fixed() = left//TODO wait中にfixedを変更するとバグが発生するおそれがある。　要チェック
      falling() = fallen append falling()
      future() = fixed() append falling()
    }
    updateTargetPosition()
  }

  def updateTargetPosition() {
    for {
      row <- falling()
      p <- row
      (tx,ty)<- future().indexOfPanelOpt(p)
    } {
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

  def updateFalling(delta: Float) {
    val (finished, continued) = falling().unzip {
      _.partition(p => {
        p.vy() += gravity * delta
        p.update(delta)
      })
    }
    //println("continued:"+continued.text)
    if (finished.exists(!_.isEmpty)) {
      //println("finished:" + finished.text)
      falling() = continued
      for (row <- finished; p <- row) {
        fixed() = fixed().updated(p.tx(), fixed()(p.tx()) :+ p)
      }
      scanRemoveFill()
    }
  }

  class AP(val n: Int) extends GMatch3.Panel with Reactor{
    val x = Var(0f)
    val y = Var(0f)
    val vx = Var(0f)
    val vy = Var(0f)
    val tx = Var(0)
    val ty = Var(0)
    lazy val isSwiping = swiping.map(_.get(AP.this).map(!_.isEmpty)|false)
    lazy val isFalling = falling.map(_.exists(_.contains(AP.this)))

    def matchTo(panel: Panel): Boolean = panel match {
      case p: AP => n == p.n
      case _ => false
    }

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
      token.reactVar(p.x)(calcPanelX(_)|>token.setX)
      token.reactVar(p.y)(calcPanelY(_)|>token.setY)
      token.setSize(panelW, panelH)
      token.setOrigin(panelW/2,panelH/2)
      tokens += token
      puzzleGroup.addActor(token)
    }
  }
  val panelRemove = (removed: Seq[ActionPuzzle3#AP]) => {
    for (panel <- removed; token <- tokens.find(_.panel == panel)) {
      tokens -= token
      token.explode{
        token.remove()
      }
    }
  }
}

class Token(val panel: ActionPuzzle3#AP, assets: AssetManager)
  extends SpriteActor(new Sprite(assets.get[Texture]("data/dummy.png")))
  with Reactor
  with ExplosionFadeout{
  import ColorTheme._
  import reactive._
  val colorMap: Int Map Varying[Color] = Map(0 -> ColorTheme.fire, 1 -> thunder, 2 -> water, 3 -> life)
  val c = (colorMap.get(panel.n) | Var(Color.WHITE))~panel.isSwiping~panel.isFalling map {
    case col~swiping~falling =>(swiping | falling)?col.cpy().mul(0.7f)|col
  }
  reactVar(c)(setColor)
  //debug()
}
