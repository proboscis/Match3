package com.glyph.scala.game.action_puzzle.view

import com.badlogic.gdx.scenes.scene2d.ui._
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.game.Glyphs
import Glyphs._
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.glyph.scala.lib.util.Logging
import com.badlogic.gdx.math.Interpolation
import com.glyph.scala.lib.libgdx.Builder

/**
 * @author glyph
 */
class Title(implicit assets: AssetManager) extends WidgetGroup with Logging {
  val skin: Skin = "skin/holo/Holo-dark-xhdpi.json".fromAssets[Skin]
  val table = new Table
  addActor(table)
  val labels = 1 to 10 map (i => new Label(i.toString, skin))
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

  table.debug()
}

object TitleBuilder extends Builder[Title] {
  def requirements: Set[(Class[_], Seq[String])] = Set(
    classOf[Skin] -> Seq("skin/holo/Holo-dark-xhdpi.json")
  )

  def create(implicit assets: AssetManager): Title = new Title()
}