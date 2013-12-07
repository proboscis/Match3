package com.glyph.scala.game.puzzle.view.match3

import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph.scala.lib.libgdx.actor.{DrawSprite, TouchSource, ExplosionFadeout}
import com.badlogic.gdx.graphics.g2d.{SpriteBatch, Sprite}
import com.glyph.scala.game.puzzle.model.match_puzzle.{Move, Life}
import com.glyph.scala.game.puzzle.model.monsters.{MonsterLike, Weapon, Monster}
import com.glyph.scala.lib.libgdx.TextureUtil
import com.glyph.scala.lib.util.reactive.{Var, Reactor, Varying}
import com.badlogic.gdx.graphics.Color
import com.glyph.scala.lib.util.json.RJSON
import com.glyph.scala.game.puzzle.model.Element.{Thunder, Water, Fire}
import com.glyph.scala.lib.puzzle.Match3
import Match3.Panel
import com.badlogic.gdx.assets.AssetManager

/**
 * @author glyph
 */
abstract class PanelToken(assets: AssetManager, val panel: Panel)
  extends Actor with DrawSprite with
  ExplosionFadeout with TouchSource with Reactor {
  val sprite: Sprite = new Sprite(TextureUtil.dummy(assets))

  import PanelToken._

  val col: Varying[Color] = panel
  reactVar(col)(setColor)

  override def remove(): Boolean = {
    stopReact(col)
    super.remove()
  }

  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    super.draw(batch, parentAlpha)
    drawSprite(batch, sprite, parentAlpha)
  }

}

object PanelToken {

  def apply(assets: AssetManager, panel: Panel): PanelToken = panel match {
    case m: Monster => new MonsterToken(assets, panel)
    case w: Weapon => new WeaponToken(assets, w)
    case _ => new ElementToken(assets, panel)
  }

  implicit def json2Str(json: RJSON): Varying[Color] = json.as[String] map {
    str => Color.valueOf(str getOrElse "ffffff")
  }

  implicit def panel2Color(p: Panel): Color = panel2VaryingColor(p)()

  import ColorTheme._

  implicit def panel2VaryingColor(p: Panel): Varying[Color] = p match {
    case _: Fire => fire
    case _: Water => water
    case _: Thunder => thunder
    case _: MonsterLike => monster
    case _: Life => life
    case _: Move => move
    case _ => Var(Color.RED)
  }
}