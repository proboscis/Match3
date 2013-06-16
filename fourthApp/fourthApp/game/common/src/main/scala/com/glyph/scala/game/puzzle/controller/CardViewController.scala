package com.glyph.scala.game.puzzle.controller

import com.glyph.scala.game.puzzle.view.{CardToken, CardView}
import com.glyph.scala.game.puzzle.model.{Game, Deck, Card}
import com.glyph.scala.lib.util.updatable.{Updatables, UpdateQueue}
import com.badlogic.gdx.scenes.scene2d.actions.{MoveToAction, Actions}
import com.badlogic.gdx.math.Interpolation
import com.glyph.scala.lib.util.collection.list.DoubleLinkedQueue
import com.glyph.scala.lib.libgdx.actor.Touchable
import com.glyph.scala.lib.math.Vec2

/**
 * @author glyph
 */
class CardViewController(view: CardView,game:Game, deck: Deck) extends Updatables {
  val updateQueue = new UpdateQueue(0.1f)
  this.add(updateQueue)
  deck.onDrawCard(
    card => {
      updateQueue.enqueue {
        createToken(card)
        setupTokenPosition()
      }
    }
  )
  val tokens = new DoubleLinkedQueue[CardToken]

  def createToken(card: Card) {
    val token = new CardToken with Touchable
    token.onPressed = (v:Vec2)=>{
      token.remove()
      tokens.remove(token)
      game.puzzle.scan()
      println(game.puzzle)
      setupTokenPosition()
      game.drawCard()
      //TODO remove from hands
    }
    token.setSize(view.getWidth / 5, view.getHeight)
    tokens.push(token)
    if (tokens.isEmpty) {
      view.addActor(token)
    } else {
      view.addActorAfter(tokens.last, token)
    }
  }

  def setupTokenPosition() {
    var i = 0
    tokens foreach {
      token => {
        val move = Actions.action(classOf[MoveToAction])
        move.setPosition((view.getWidth / 5) * i, 0)
        move.setDuration(0.5f)
        move.setInterpolation(Interpolation.exp10Out)
        token.clearActions()
        token.addAction(move)
        i += 1
      }
    }
  }
}
