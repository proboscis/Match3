package com.glyph._scala.lib.libgdx.actor

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.glyph._scala.lib.util.{Logging, Animated}
import com.badlogic.gdx.scenes.scene2d.Actor
import com.esotericsoftware.tablelayout.Cell
import com.glyph._scala.lib.util.updatable.task._
import com.badlogic.gdx.math.Interpolation

/**
 * actors added through "add" method will be animated when this Table's animation is invoked.
 */
class AnimatedTable extends Table with Animated with Logging with Tasking {

  import com.glyph._scala.game.Glyphs
  import Glyphs._

  case class Holder(actor: Actor) extends SameSize {
    addActor(actor)
  }

  val actorLayouts = collection.mutable.ArrayBuffer[Holder]()

  override def add(actor: Actor): Cell[_] = {
    val holder = Holder(actor)
    actorLayouts += holder
    super.add(holder)
  }

  override def removeActor(actor: Actor): Boolean = {
    val i = actorLayouts.indexWhere(_.actor == actor)
    if (i >= 0) actorLayouts.remove(i)
    super.removeActor(actor)
  }


  override def clearChildren(): Unit = {
    super.clearChildren()
    actorLayouts.clear()
  }

  override def layout(): Unit = {
    super.layout()
    for (holder@Holder(actor) <- actorLayouts) {
      actor.setSize(holder.getWidth, holder.getHeight)
    }
  }

  def setPositions(f: Holder => (Float, Float)) {
    for (holder@Holder(actor) <- actorLayouts) {
      val (x, y) = f(holder)
      actor.setPosition(x, y)
    }
  }

  def moveToPositions(f: Holder => (Float, Float), duration: Float = 0.5f)(cb: () => Unit) {
    val size = actorLayouts.size
    var i = 0
    val par = Parallel()
    for (holder@Holder(actor) <- actorLayouts) {
      import Interpolation._
      val (x, y) = f(holder)
      val offset = duration / size * i
      par.add(Sequence(Delay(offset), Interpolate(actor) of ActorAccessor.XY to(x, y) in (duration - offset) using exp10Out))
      i += 1
    }
    add(Sequence(par, Do(cb)))
  }

  def moveToCellPositions(cb: () => Unit) = moveToPositions {
    case holder@Holder(actor) => (0, 0)
  }(cb)


  def in(cb: () => Unit) {
    setPositions {
      case holder@Holder(actor) => (getWidth, 0)
    }
    moveToCellPositions(cb)
  }

  def out(cb: () => Unit) {
    moveToPositions({
      case holder@Holder(actor) => (-getWidth,0)
    }, 0.2f)(cb)
  }

  def pause(cb: () => Unit): Unit = out(cb)

  def resume(cb: () => Unit) {
    setPositions {
      case holder@Holder(actor) => (-getWidth,0)
    }
    moveToCellPositions(cb)
  }
}

