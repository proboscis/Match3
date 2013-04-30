package com.glyph.scala.game.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph.libgdx.asset.AM
import com.badlogic.gdx.graphics.g2d.{SpriteBatch, Sprite}
import com.badlogic.gdx.graphics.Texture
import com.glyph.scala.lib.math.Vec2
import com.glyph.libgdx.Engine
import com.glyph.libgdx.particle.{SpriteExplosion, SpriteParticle, ParticlePool}

/**
 * @author glyph
 */
class CardToken(pool:ParticlePool[SpriteParticle]) extends Actor with Touchable{
  val sprite = new Sprite(AM.instance().get[Texture]("data/card1.png"))
  sprite.setSize(Engine.VIRTUAL_WIDTH/5,Engine.VIRTUAL_WIDTH/5)
  //you must set the actor's size in order to receive touch events
  setSize(sprite.getWidth,sprite.getHeight)
  override def draw(batch: SpriteBatch, parentAlpha: Float){
    super.draw(batch, parentAlpha)
    sprite.setPosition(getX,getY)
    sprite.setRotation(getRotation)
    sprite.setScale(getScaleX,getScaleY)
    sprite.draw(batch,parentAlpha)
  }
  onPressed = (pos:Vec2)=>{
    getParent.addActor(new SpriteExplosion(sprite,pool))
    remove()
    println("removed")
    true
  }
}
