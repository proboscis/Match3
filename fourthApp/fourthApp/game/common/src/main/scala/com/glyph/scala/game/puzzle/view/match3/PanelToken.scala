package com.glyph.scala.game.puzzle.view.match3

import com.badlogic.gdx.graphics._
import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph.scala.lib.libgdx.actor.{TouchSource, ExplosionFadeout, OldDrawSprite}
import com.badlogic.gdx.graphics.g2d.Sprite
import com.glyph.scala.game.puzzle.model.match_puzzle.Panel
import com.glyph.java.particle.{SpriteParticle, ParticlePool}
import com.glyph.scala.game.puzzle.model.monsters.Monster

/**
 * @author glyph
 */
abstract class PanelToken(val panel: Panel) extends Actor with OldDrawSprite with ExplosionFadeout with TouchSource {
  val sprite: Sprite = new Sprite(PanelToken.texture)

  override def explode(f: => Unit) {
    super.explode(f)
  }
}

object PanelToken {
  val main = "2651BD"
  val image = new Pixmap(128, 128, Pixmap.Format.RGBA8888)
  image.setColor(Color.WHITE)
  image.fillRectangle(0, 0, image.getWidth, image.getHeight)
  val texture = new Texture(image)
  val pool = new ParticlePool(classOf[SpriteParticle], 1000)

  def apply(panel: Panel): PanelToken = panel match{
    case m:Monster => new MonsterToken(panel)
    case _=>new ElementToken(panel)
  }
}
