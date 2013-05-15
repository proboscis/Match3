package com.glyph.scala.game.view

import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph.libgdx.asset.AM
import com.badlogic.gdx.graphics.g2d.{SpriteBatch, Sprite}
import com.badlogic.gdx.graphics.Texture
import com.glyph.scala.lib.math.Vec2
import com.glyph.libgdx.particle.{SpriteExplosion, SpriteParticle, ParticlePool}
import com.glyph.scala.game.card.Card
import com.badlogic.gdx.math.MathUtils
import com.glyph.scala.lib.util.actor.Touchable

/**
 * @author glyph
 */
class CardToken(val card:Card,pool:ParticlePool[SpriteParticle]) extends Actor with Touchable{
  val sprite = new Sprite(AM.instance().get[Texture]("data/card"+MathUtils.random(1,10)+".png"))
  //sprite.setSize(Engine.VIRTUAL_WIDTH/5,Engine.VIRTUAL_WIDTH/5)
  //you must set the actor's size in order to receive touch events
  //setSize(sprite.getWidth,sprite.getHeight)
  override def draw(batch: SpriteBatch, parentAlpha: Float){
    super.draw(batch, parentAlpha)
    sprite.setPosition(getX,getY)
    sprite.setRotation(getRotation)
    sprite.setScale(getScaleX,getScaleY)
    sprite.setSize(getWidth,getHeight)
    sprite.draw(batch,parentAlpha)
  }
  onPressed = (pos:Vec2)=>{
    getParent.addActor(new SpriteExplosion(sprite,pool))
    remove()
    true
  }
}
