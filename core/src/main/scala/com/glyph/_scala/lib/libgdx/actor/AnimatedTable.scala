package com.glyph._scala.lib.libgdx.actor

import com.badlogic.gdx.scenes.scene2d.ui.{Widget, Image, Table}
import com.glyph._scala.lib.util.{Logging, Animated}
import com.badlogic.gdx.scenes.scene2d.{Group, Actor}
import com.esotericsoftware.tablelayout.Cell
import com.glyph._scala.lib.util.updatable.task._
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.glyph._scala.lib.libgdx.actor.table.AnimatedBuilderHolder.AnimatedActor

/**
 * actors added through "add" method will be animated when this Table's animation is invoked.
 */
class AnimatedTable(implicit processor: ParallelProcessor) extends Table with Animated with Logging {
  debug()

  import com.glyph._scala.game.Glyphs
  import Glyphs._


  case class Holder(actor: Actor) extends SameSize{
    addActor(actor)

  }

  val actorLayouts = collection.mutable.ArrayBuffer[Holder]()
  private var bgHolder = Holder(new Image(getBackground))
  actorLayouts += bgHolder
  val animatedActors = collection.mutable.ArrayBuffer[AnimatedActor]()

  private def setupBGHolder() {
    log("setupBGHolder", getX, getY, getWidth, getHeight)
    bgHolder.setBounds(getX, getY, getWidth, getHeight)
  }

  override def setBackground(background: Drawable): Unit = {
    //super.setBackground(background)
    actorLayouts -= bgHolder
    removeActor(bgHolder)
    bgHolder = Holder(new Image(background))
    addActor(bgHolder)
    setupBGHolder()
    actorLayouts += bgHolder
  }

  override def add(actor: Actor): Cell[_] = {
    actor match {

    case aa:AnimatedActor =>{
      animatedActors += aa
      super.add(aa)
    }
    case actor => {
        val holder = Holder(actor)
        actorLayouts += holder
        super.add(holder)
      }
    }
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
    setupBGHolder()
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
    //somehow, out is called first...!!?
    var task: Task = null
    task = Sequence(
      Block {
        log("task:" + task.hashString)
        log("started moveToPositions:" + cb.hashString)
      },
      par,
      Do(() => {
        log("moveToPositions:Done:" + task.hashString)
        log("calling back:" + cb.hashString)
        cb()
        log("called back:" + cb.hashString)
      }))
    processor.add(task)
    log("moveToPositions:Task:" + task.hashString)
    log("moveToPositions:" + cb.hashString)
  }

  def moveToCellPositions(cb: () => Unit) = moveToPositions {
    case holder@Holder(actor) => (0, 0)
  }(cb)

  // you have to take care of the exception that out is called before in finishes

  def in(cb: () => Unit) {
    animatedActors.foreach(_.in(() => {}))
    log("in:" + cb.hashString)
    log("self:" + this.hashString)
    setPositions {
      case holder@Holder(actor) => (getWidth, 0)
    }
    moveToCellPositions(cb)
  }

  def out(cb: () => Unit) {
    animatedActors.foreach(_.out(() => {}))
    log("out:" + cb.hashString)
    moveToPositions({
      case holder@Holder(actor) => (-getWidth, 0)
    }, 0.2f)(cb)
  }

  def pause(cb: () => Unit): Unit = {
    log("pause")
    out(cb)
  }

  def resume(cb: () => Unit) {
    animatedActors.foreach(_.resume(() => {}))
    log("resume")
    setPositions {
      case holder@Holder(actor) => (-getWidth, 0)
    }
    moveToCellPositions(cb)
  }


  override def setParent(parent: Group): Unit = {
    log(if (parent == null) "removed" else "added")
    super.setParent(parent)
  }
}

object AnimatedTable {

  import scalaz._
  import Scalaz._

  def apply(layout: Cell[_] => Unit)(actors: Actor*)(implicit processor: ParallelProcessor): AnimatedTable = (new AnimatedTable /: actors) {
    case (table, actor) => table <| (_.add(actor) |> layout)
  }
}
