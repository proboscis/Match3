package com.glyph.scala.game.puzzle.view

import com.badlogic.gdx.scenes.scene2d.ui.{WidgetGroup, Table}
import com.glyph.scala.game.puzzle.model.Game
import com.glyph.scala.lib.libgdx.actor.ui.{Reaction, Gauge, RLabel}
import com.glyph.scala.lib.util.reactive.RJS
import com.glyph.scala.lib.libgdx.reactive.GdxFile
import com.glyph.scala.lib.libgdx.actor.Layered
import com.badlogic.gdx.scenes.scene2d.Action
import com.glyph.scala.lib.libgdx.actor.action.MyActions

/**
 * @author glyph
 */
class HeaderView(game: Game) extends Table {
  //TODO　リセット時に配置がずれる問題の解決
  val script = new RJS[Any](new GdxFile("js/view/headerView.js").getString,
    ("table" -> this) ::
      ("root" -> new WidgetGroup with Layered) ::
      ("levelLabel" -> new RLabel(skin, game.player.position map {
        "floor:" + _ + "/" + game.dungeon.goal
      }) with Reaction[String]{
        def reaction: Action = {
          MyActions.jump(50, 0.6f)
        }
      }) ::
      ("gauge" -> new Gauge(game.player.experience map {
        exp => (exp % 1000) / 1000f
      })) :: Nil)

  /*
  add( new Gauge(game.experience -> {
    exp => println(exp);(exp % 100) / 100f
  })).fill.center
  */
}
