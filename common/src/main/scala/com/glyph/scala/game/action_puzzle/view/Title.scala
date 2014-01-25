package com.glyph.scala.game.action_puzzle.view

import com.badlogic.gdx.scenes.scene2d.ui._
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.game.Glyphs
import Glyphs._
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.glyph.scala.lib.util.{Animated, Logging}
import com.badlogic.gdx.math.Interpolation
import com.glyph.scala.lib.libgdx.Builder
import com.badlogic.gdx.scenes.scene2d.Actor

object Title{
  def apply(label:String=>Actor) = new WidgetGroup with Animated with Logging{
    val table = new Table
    table.debug()
    addActor(table)
    val labels = 1 to 10 map (_.toString) map label
    labels foreach addActor
    val pairs = labels map {
      l => val cell = table.add()
        cell.fill.expand().row()
        l -> cell
    }

    override def layout(): Unit = {
      super.layout()
      table.setSize(getWidth, getHeight)
      table.layout()
      pairs.zipWithIndex.foreach {
        case t@(pair, i) =>
          import Actions._
          val (label, cell) = pair

          label.clearActions()
          label.addAction(
            sequence(
              delay(i * 0.1f),
              moveTo(cell.getWidgetX, cell.getWidgetY, 0.3f, Interpolation.exp10Out)))

          log(cell.getWidgetX, cell.getWidgetY)
      }
    }

    def in(cb: () => Unit): Unit = ???

    def out(cb: () => Unit): Unit = ???

    def pause(cb: () => Unit): Unit = ???

    def resume(cb: () => Unit): Unit = ???
  }
  def dummy:Builder[Actor with Animated] = ???
}

class TitleBuilder extends Builder[Actor with Animated] {
  def requirements: Set[(Class[_], Seq[String])] = Set(
    classOf[Skin] -> Seq("skin/holo/Holo-dark-xhdpi.json")
  )
  def create(implicit assets: AssetManager) = ???
}