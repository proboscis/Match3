package com.glyph.scala.game.puzzle.view.match3

import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph.scala.lib.libgdx.actor.{DrawSprite, TouchSource, ExplosionFadeout}
import com.badlogic.gdx.graphics.g2d.{SpriteBatch, Sprite}
import com.glyph.scala.game.puzzle.model.match_puzzle.{Move, Life, Panel}
import com.glyph.java.particle.{SpriteParticle, ParticlePool}
import com.glyph.scala.game.puzzle.model.monsters.{MonsterLike, Weapon, Monster}
import com.glyph.scala.lib.libgdx.TextureUtil
import com.glyph.scala.lib.util.reactive.{Var, Reactor, Varying}
import com.badlogic.gdx.graphics.Color
import com.glyph.scala.lib.util.json.RJSON
import com.glyph.scala.game.puzzle.model.Element.{Thunder, Water, Fire}

/**
 * @author glyph
 */
abstract class PanelToken(val panel: Panel)
  extends Actor with DrawSprite with
  ExplosionFadeout with TouchSource with Reactor{
  val sprite: Sprite = new Sprite(TextureUtil.dummy)

  import PanelToken._
  val col:Varying[Color] = panel
  reactVar(col)(setColor)

  override def remove(): Boolean = {
    stopReact(col)
    super.remove()
  }

  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    super.draw(batch, parentAlpha)
    drawSprite(batch, sprite, parentAlpha)
  }

  override def explode(f: => Unit) {
    super.explode(f)
  }
}

object PanelToken {
  val pool = new ParticlePool(classOf[SpriteParticle], 1000)

  def apply(panel: Panel): PanelToken = panel match {
    case m: Monster => new MonsterToken(panel)
    case w: Weapon => new WeaponToken(w)
    case _ => new ElementToken(panel)
  }
  implicit def json2Str(json:RJSON):Varying[Color] = json.as[String] map {str => Color.valueOf(str getOrElse "ffffff")}
  implicit def panel2Color(p:Panel):Color =panel2VaryingColor(p)()
  import ColorTheme._
  implicit def panel2VaryingColor (p:Panel):Varying[Color] = p match{
    case _:Fire => fire
    case _:Water => water
    case _:Thunder => thunder
    case _:MonsterLike => monster
    case _:Life => life
    case _:Move => move
    case _ => Var(Color.RED)
  }
}