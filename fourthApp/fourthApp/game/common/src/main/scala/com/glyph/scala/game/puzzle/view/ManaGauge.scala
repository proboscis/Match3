package com.glyph.scala.game.puzzle.view

import com.glyph.scala.lib.util.reactive.{Reactor, Varying, RFile, Divider}
import com.badlogic.gdx.scenes.scene2d.ui.{WidgetGroup, Table}
import com.glyph.scala.lib.libgdx.actor.ui.{Gauge, Reaction, RLabel}
import com.badlogic.gdx.scenes.scene2d.Action
import com.glyph.scala.lib.libgdx.actor.action.MyActions
import com.glyph.scala.lib.util.json.RJSON
import com.glyph.scala.lib.libgdx.actor.{Layered, ReactiveSize}
import com.badlogic.gdx.graphics.Color
import com.glyph.scala.lib.libgdx.reactive.GdxFile
import com.glyph.scala.game.puzzle.view.match3.ElementToken

/**
 * @author glyph
 */
class ManaGauge(mana: Varying[Int],color:Varying[Color]) extends WidgetGroup with Layered with Reactor{

  val config = RJSON(GdxFile("js/view/manaGauge.js").getString)
  val label = new RLabel(skin, mana.map {
    _ + ""
  }) with Reaction {
    def reaction: Action = (for {
      height <- config().height.as[Float]
      duration <- config().duration.as[Float]
    } yield {
      MyActions.jump(height, duration)
    }) getOrElse MyActions.NullAction
  }
  reactVar(color)(label.setColor)
  addActor(label)

  override def setColor(color: Color) {
    super.setColor(color)
  }
}
