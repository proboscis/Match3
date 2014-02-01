package com.glyph.scala.game.action_puzzle.view

import com.badlogic.gdx.scenes.scene2d.ui._
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.lib.util.{Animated, Logging}
import com.badlogic.gdx.math.Interpolation
import com.glyph.scala.lib.libgdx.Builder
import com.badlogic.gdx.scenes.scene2d.Actor
import scala.language.existentials
import scalaz._
import Scalaz._
import com.badlogic.gdx.scenes.scene2d.actions.Actions._
import com.glyph.scala.lib.libgdx.actor.action.ActionOps

object Title {
  def apply(title: String,labelConstructor: String => Actor, startCallback: () => Unit) = new WidgetGroup with Animated with Logging {
    val table = new Table
    table.debug()
    addActor(table)
    val label = labelConstructor(title) <| addActor
    val cell = table.add() <| (_.fill.expand.row)

    def animation(cb: () => Unit = () => {}) {
      log(s"start animation:${cell.getWidgetX},${cell.getWidgetY}")
      label.clearActions()
      label.addAction(
        sequence(
          ActionOps.run(()=>{label.setPosition(400,400)}),
          moveTo(cell.getWidgetX, cell.getWidgetY, 0.3f, Interpolation.exp10Out),
          ActionOps.run(cb)
        )
      )
    }

    override def layout(): Unit = {
      super.layout()
      table.setSize(getWidth, getHeight)
      table.layout()
      animation()
    }

    def in(cb: () => Unit): Unit = animation(cb)
    def out(cb: () => Unit): Unit = animation(cb)
    def pause(cb: () => Unit): Unit = animation(cb)
    def resume(cb: () => Unit): Unit = animation(cb)
  }
}

class TitleBuilder extends Builder[Actor with Animated] {
  def requirements: Set[(Class[_], Seq[String])] = Set(
    classOf[Skin] -> Seq("skin/holo/Holo-dark-xhdpi.json")
  )

  def create(implicit assets: AssetManager) = ???
}