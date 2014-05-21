package com.glyph._scala.test

import com.glyph._scala.lib.libgdx.screen.ConfiguredScreen
import com.badlogic.gdx.graphics.g2d.{Batch, ParticleEffect}
import com.sun.org.apache.bcel.internal.generic.NEW
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor

/**
 * @author glyph
 */
class GdxParticleTest extends ConfiguredScreen{
  val effect = new ParticleEffect()
  log("load effect")
  effect.load(Gdx.files.internal("data/particle.json"),Gdx.files.internal("data"))
  effect.start()
  effect.setPosition(STAGE_WIDTH/2,STAGE_HEIGHT/2)
  root.add(new Actor{
    override def draw(batch: Batch, parentAlpha: Float){
      super.draw(batch,parentAlpha)
      effect.draw(batch)
    }
  }).fill.expand
}
