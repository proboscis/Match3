package com.glyph.scala.game.ui

import com.glyph.scala.game.card.{Card, CardDeque}
import com.glyph.scala.lib.util.{Scissor, Disposable}
import com.badlogic.gdx.scenes.scene2d.Group
import com.glyph.scala.game.card.CardDeque.DrawCard
import com.glyph.scala.lib.math.Vec2
import com.glyph.libgdx.particle.{SpriteParticle, ParticlePool}
import collection.mutable.ListBuffer
import com.badlogic.gdx.scenes.scene2d.actions.{Actions, MoveToAction}
import com.badlogic.gdx.math.Interpolation

/**
 * UI class
 * @author glyph
 */
class CardTable(deque: CardDeque, pool: ParticlePool[SpriteParticle]) extends Group with Disposable with Touchable with Scissor {
  deque.register(dequeCallback)
  val tokens = ListBuffer.empty[CardToken]

  def dequeCallback(deque: CardDeque, value: Any) {
    value match {
      case DrawCard(card) => {
        createToken(card)
        setupTokenTargets()
      }
      case _ =>
    }
  }

  def createToken(card: Card) {
    println("createToken!")
    val token = new CardToken(card, pool)
    token.setSize(getWidth / 5, getWidth / 5 * 1.618f)
    val func = token.onPressed
    token.onPressed = (pos: Vec2) => {
      func(pos)
      tokens -= token
      deque.drawCard()
      true
    }
    addActor(token)
    tokens += token
  }

  def setupTokenTargets() {
    println("setup token")
    var i = 0;
    tokens.foreach {
      token => {
        val action = Actions.action(classOf[MoveToAction])
        val w = token.getWidth
        action.setPosition(w * (i % 5), i / 5 * token.getHeight)
        token.clearActions()
        token.addAction(action)
        action.setInterpolation(Interpolation.exp10Out)
        action.setDuration(0.4f)
        i += 1
      }
    }
  }

  def dispose() {
    deque.unregister(dequeCallback)
  }

  onPressed = (pos: Vec2) => {
    println("pressed" + pos)
    true
  }
}
