package com.glyph.scala.game.puzzle.view

import com.glyph.scala.game.puzzle.model.Game
import com.badlogic.gdx.scenes.scene2d.ui.{Table, Image, WidgetGroup}
import com.badlogic.gdx.graphics.g2d.{TextureRegion, SpriteBatch}
import com.glyph.scala.lib.libgdx.TextureUtil
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Scaling
import com.glyph.scala.lib.libgdx.actor.Gauge
import com.glyph.scala.lib.util.observer.reactive.Reactor

/**
 * @author glyph
 */
class StatusView(game: Game) extends Table with Reactor{
  val lifeGauge = new Gauge(game.player.hp()/100f)
  add(lifeGauge).expand.fill(0.95f,0.6f)
}
