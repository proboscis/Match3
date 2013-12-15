package com.glyph.scala.game.puzzle.view

import com.glyph.scala.game.puzzle.model.{PlayableDeck, Game}
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.glyph.scala.lib.libgdx.actor.{Tasking}
import com.glyph.scala.lib.util.reactive
import reactive._
import com.badlogic.gdx.math.Interpolation
import com.glyph.scala.lib.util.updatable.reactive.DeprecatedEasing
import com.glyph.scala.lib.libgdx.reactive.GdxFile
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.lib.libgdx.actor.widgets.Layered

/**
 * @author glyph
 */
class StatusView(assets:AssetManager,game: Game, deck: PlayableDeck[_]) extends Table with Reactor with Tasking {
  debug()
  val visualLife = DeprecatedEasing(this)(game.player.hp)(_ / 100f * 0.7f + 0.3f, 0)(Interpolation.linear) map {
    _.toInt
  }
  val gaugeAlpha = game.player.hp map {
    _ / 100f
  }
  val lifeText = visualLife map {
    _ + "/100"
  }
  val deckText = deck.deck map {
    _.size + ""
  }
  val discardText = deck.discarded map {
    _.size + ""
  }
  val script = new RJS[Any](GdxFile("js/view/statusView.js"),
    ("gaugeAlpha" -> gaugeAlpha) ::
      ("lifeText" -> lifeText) ::
      ("deckText" -> deckText) ::
      ("dadText" -> (deckText ~ discardText map {
        case a ~ b => a + "/" + b
      })) ::
      ("discardText" -> discardText) ::
      ("skin" -> skin(assets)) ::
      ("table" -> this) ::
      ("layers" -> new Layered {}) ::
      ("wrapper" -> new Table) :: Nil)
  /*
  layers.addActor(new Gauge(gaugeAlpha))
  wrapper.add(new RLabel(skin, lifeText)).center()
  layers.addActor(wrapper)
  add(new RLabel(skin,deckText)).expand(1, 1)
  add(layers).expand(7, 1).fill(1f, 0.9f)
  add(new RLabel(skin,discardText)).expand(1, 1)
  */
}
