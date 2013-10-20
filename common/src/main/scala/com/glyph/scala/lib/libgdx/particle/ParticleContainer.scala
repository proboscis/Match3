package com.glyph.scala.lib.libgdx.particle

import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph.java.particle.{ParticlePool, SpriteParticle}
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Disposable

trait ParticleContainer extends Actor with Disposable{
  var particles:Seq[SpriteParticle] = Nil
  def pool:ParticlePool[SpriteParticle]
  def +=(p:SpriteParticle){particles +:=p}

  override def act(delta: Float) {
    super.act(delta)
    particles foreach{
      p =>
        p.update(delta)
    }
  }

  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    super.draw(batch, parentAlpha)
    particles foreach {_.draw(batch,parentAlpha)}
  }

  def dispose() {
    particles foreach pool.free
    particles = Nil
  }
}