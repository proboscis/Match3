package com.glyph.scala.game.action_puzzle.view.animated

import com.badlogic.gdx.scenes.scene2d.ui.{Label, Skin, Table}
import com.glyph.scala.lib.util.{Logging, Animated}
import com.glyph.scala.lib.libgdx.actor.transition.AnimatedManager
import com.badlogic.gdx.scenes.scene2d.Actor
import com.esotericsoftware.tablelayout.Cell
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.math.Interpolation
import com.glyph.scala.lib.libgdx.actor.transition.AnimatedManager.AnimatedConstructor

/**
 * @author glyph
 */
class Menu {

}

object Menu {
  import scalaz._
  import Scalaz._
  val constructor: Skin => AnimatedConstructor = skin => info => callbacks => new AnimatedTable{
    debug()
    def label(any:Any) = new Label(any.toString,skin)
    1 to 10 map label foreach (add(_).expand.fill.row())
  }
}

class AnimatedTable extends Table with Animated with Logging {

  import scalaz._
  import Scalaz._

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

  def moveToPositions(f: (Actor, Cell[_]) => (Float, Float)) {
    val size = actorLayouts.size
    var i = 0
    val duration = 0.5f
    for ((actor, cell) <- actorLayouts) {
      import Actions._
      import Interpolation._
      actor.clearActions()
      val (x, y) = f(actor, cell)
      val offset = duration/size * i
      actor.addAction(sequence(
        delay(offset),
        moveTo(x, y, duration - offset, exp10Out)
      ))
      i += 1
    }
  }

  def moveToCellPositions() = moveToPositions((actor, cell) => (cell.getWidgetX, cell.getWidgetY))

  def in(cb: () => Unit) {
    setPositions((actor, cell) => (getWidth, cell.getWidgetY))
    moveToCellPositions()
  }

  def out(cb: () => Unit) {
    moveToPositions((actor, cell) => (-actor.getWidth, cell.getWidgetY))
  }

  def pause(cb: () => Unit): Unit = out(cb)

  def resume(cb: () => Unit) {
    setPositions((actor, cell) => (-actor.getWidth, cell.getWidgetY))
    moveToCellPositions()
  }
}

