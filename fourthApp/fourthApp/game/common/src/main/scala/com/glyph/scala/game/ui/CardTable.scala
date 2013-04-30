package com.glyph.scala.game.ui

import com.glyph.scala.game.card.CardDeque
import com.glyph.scala.lib.util.Disposable
import com.badlogic.gdx.scenes.scene2d.{Actor, Group, Stage}
import com.glyph.scala.game.card.CardDeque.DrawCard
import com.glyph.scala.lib.math.Vec2
import com.glyph.libgdx.particle.{SpriteParticle, ParticlePool}

/**
 * @author glyph
 */
class CardTable(deque: CardDeque,pool:ParticlePool[SpriteParticle]) extends Group with Disposable with Touchable{
  deque.register(dequeCallback)
  def dequeCallback(deque: CardDeque, value: Any) {
    value match{
      case DrawCard(card)=>{
        val token = new CardToken(pool)
        addActor(token)
      }
      case _ =>
    }
  }
  def dispose() {
    deque.unregister(dequeCallback)
  }
  onPressed = (pos:Vec2)=>{
    println("pressed"+pos)
    true
  }
}
