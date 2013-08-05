package com.glyph.scala.game.puzzle.view

import com.glyph.scala.game.puzzle.model.Game
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.glyph.scala.lib.libgdx.actor.ui.{RLabel, Gauge}
import com.glyph.scala.lib.libgdx.actor.Layered
import com.glyph.scala.lib.util.reactive
import reactive._

/**
 * @author glyph
 */
class StatusView(game: Game) extends Table with Reactor {
  debug()
  val lifeGauge = new Gauge(game.player.hp->{_/100f})
  //TODO show deck status
  val layers = new Layered {}
  layers.addActor(lifeGauge)
  val wrapper = new Table()
  wrapper.add(new RLabel(skin, game.player.hp->{_+"/100"})).center()
  layers.addActor(wrapper)
  add(new RLabel(skin, game.deck.deck->{_.size+""})).expand(1,1)
  add(layers).expand(7,1).fill(0.95f, 0.8f)
  add(new RLabel(skin,game.deck.discarded->{_.size+""})).expand(1,1)
}
