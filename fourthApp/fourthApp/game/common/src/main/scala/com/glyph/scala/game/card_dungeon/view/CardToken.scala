package com.glyph.scala.game.view

import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph.java.asset.AM
import com.badlogic.gdx.graphics.g2d.{SpriteBatch, Sprite}
import com.badlogic.gdx.graphics.Texture
import com.glyph.scala.lib.math.Vec2
import com.glyph.java.particle.{SpriteExplosion, SpriteParticle, ParticlePool}
import com.glyph.scala.game.card.Card
import com.badlogic.gdx.math.MathUtils
import com.glyph.scala.lib.libgdx.actor.Touchable

/**
 * @author glyph
 */
class CardToken(val card: Card, pool: ParticlePool[SpriteParticle]) extends Actor with Touchable {
  val sprite = new Sprite(AM.instance().get[Texture]("data/card" + MathUtils.random(1, 10) + ".png"))

  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    super.draw(batch, parentAlpha)
    sprite.setPosition(getX, getY)
    sprite.setRotation(getRotation)
    sprite.setScale(getScaleX, getScaleY)
    sprite.setSize(getWidth, getHeight)
    sprite.draw(batch, parentAlpha)
  }

  onPressed = (x,y) => {
    getParent.addActor(new SpriteExplosion(sprite, pool))
    remove()
    true
  }
}
