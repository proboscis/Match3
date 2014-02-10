package com.glyph._scala.lib.libgdx.actor

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.glyph._scala.lib.util.{Logging, Animated}
import com.badlogic.gdx.scenes.scene2d.Actor
import com.esotericsoftware.tablelayout.Cell
import com.glyph._scala.lib.util.updatable.task._
import com.badlogic.gdx.math.Interpolation


class AnimatedTable extends Table with Animated with Logging with Tasking{
  import scalaz._
  import Scalaz._
  import com.glyph._scala.lib.util.pool.GlobalPool.globals
  import com.glyph._scala.game.Glyphs
  import Glyphs._
  val actorLayouts = collection.mutable.ArrayBuffer[(Actor,Cell[_])]()

  override def add(actor: Actor): Cell[_] = {
    addActor(actor)
    val cell = super.add()
    actorLayouts += (actor->cell)
    cell
  }


  override def layout(): Unit = {
    super.layout()
    for((actor,cell) <- actorLayouts){
      actor.setSize(cell.getWidgetWidth,cell.getWidgetHeight)
    }
  }

  def setPositions(f: (Actor, Cell[_]) => (Float, Float)) {
    for ((actor, cell) <- actorLayouts) {
      val (x, y) = f(actor, cell)
      actor.setPosition(x, y)
    }
  }

  def moveToPositions(f: (Actor, Cell[_]) => (Float, Float),duration:Float = 0.5f)(cb:()=>Unit) {
    val size = actorLayouts.size
    var i = 0
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
    moveToPositions((actor, cell) => (-actor.getWidth, cell.getWidgetY),0.2f)(cb)
  }

  def pause(cb: () => Unit): Unit = out(cb)

  def resume(cb: () => Unit) {
    setPositions((actor, cell) => (-actor.getWidth, cell.getWidgetY))
    moveToCellPositions(cb)
  }
}

