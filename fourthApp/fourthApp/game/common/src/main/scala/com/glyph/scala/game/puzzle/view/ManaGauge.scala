package com.glyph.scala.game.puzzle.view

import com.glyph.scala.lib.util.reactive.{RFile, Divider}
import com.badlogic.gdx.scenes.scene2d.ui.{WidgetGroup, Table}
import com.glyph.scala.lib.libgdx.actor.ui.{Gauge, Reaction, RLabel}
import com.badlogic.gdx.scenes.scene2d.Action
import com.glyph.scala.lib.libgdx.actor.action.MyActions
import com.glyph.scala.lib.util.json.RJSON
import com.glyph.scala.lib.libgdx.actor.{Layered, ReactiveSize}
import com.badlogic.gdx.graphics.Color
import com.glyph.scala.lib.libgdx.reactive.GdxFile

/**
 * @author glyph
 */
class ManaGauge(mana: Divider[Float]) extends WidgetGroup with Layered{
  val config = RJSON(GdxFile("js/view/manaGauge.js").getString)
  import com.glyph.scala.lib.util.reactive.~
  val gauge = new Gauge(mana.source~mana.limit map{case s~limit=>s/limit},true)
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
  addActor(gauge)
  addActor(label)

  override def setColor(color: Color) {
    super.setColor(color)
    gauge.setColor(color)
  }
}
