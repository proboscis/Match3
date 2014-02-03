package com.glyph.scala.game.action_puzzle.view.animated

import com.badlogic.gdx.scenes.scene2d.ui.{TextButton, Label, Skin, Table}
import com.glyph.scala.lib.util.{Logging, Animated}
import com.glyph.scala.lib.libgdx.actor.transition.AnimatedManager
import com.badlogic.gdx.scenes.scene2d.Actor
import com.esotericsoftware.tablelayout.Cell
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.math.Interpolation
import com.glyph.scala.lib.libgdx.actor.transition.AnimatedManager.AnimatedConstructor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.glyph.scala.lib.libgdx.actor.action.ActionOps
import com.glyph.scala.lib.libgdx.actor.Tasking
import com.glyph.scala.lib.util.updatable.task._


object Menu {
  import scalaz._
  import Scalaz._
  val constructor: Skin => AnimatedConstructor = skin => info => callbacks => new AnimatedTable{
    debug()
    def label(any:Any) = new TextButton(any.toString,skin) <| (_.addListener(new ChangeListener{
      def changed(p1: ChangeEvent, p2: Actor){
        callbacks(any.toString)(Map())
      }
    }))
    1 to 10 map label foreach (add(_).expand.fill.row())
  }
}

class AnimatedTable extends Table with Animated with Logging with Tasking{
  import scalaz._
  import Scalaz._
  import com.glyph.scala.lib.util.pool.GlobalPool.globals
  import com.glyph.scala.game.Glyphs
  import Glyphs._
  val actorLayouts = collection.mutable.ArrayBuffer[(Actor,Cell[_])]()

  override def add(actor: Actor): Cell[_] = {
    addActor(actor)
    val cell = super.add()
    actorLayouts += (actor->cell)
    cell
  }

  def setPositions(f: (Actor, Cell[_]) => (Float, Float)) {
    for ((actor, cell) <- actorLayouts) {
      val (x, y) = f(actor, cell)
      actor.setPosition(x, y)
    }
  }

  def moveToPositions(f: (Actor, Cell[_]) => (Float, Float))(cb:()=>Unit) {
    val size = actorLayouts.size
    var i = 0
    val duration = 0.5f
    val par = Parallel()
    for ((actor, cell) <- actorLayouts) {
      import Interpolation._
      actor.clearActions()
      val (x, y) = f(actor, cell)
      val offset = duration/size * i
      par.add(Sequence(Delay(offset),Interpolate(actor) of ActorAccessor.XY to (x,y) in (duration-offset) using exp10Out))
      i += 1
    }
    add(Sequence(par,Do(cb)))
  }

  def moveToCellPositions(cb:()=>Unit) = moveToPositions((actor, cell) => (cell.getWidgetX, cell.getWidgetY))(cb)

  def in(cb: () => Unit) {
    setPositions((actor, cell) => (getWidth, cell.getWidgetY))
    moveToCellPositions(cb)
  }

  def out(cb: () => Unit) {
    moveToPositions((actor, cell) => (-actor.getWidth, cell.getWidgetY))(cb)
  }

  def pause(cb: () => Unit): Unit = out(cb)

  def resume(cb: () => Unit) {
    setPositions((actor, cell) => (-actor.getWidth, cell.getWidgetY))
    moveToCellPositions(cb)
  }
}

