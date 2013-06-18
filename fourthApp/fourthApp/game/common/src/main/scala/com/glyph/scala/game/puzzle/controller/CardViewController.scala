package com.glyph.scala.game.puzzle.controller

import com.glyph.scala.game.puzzle.view.{CardToken, CardView}
import com.glyph.scala.game.puzzle.model.{Game, Deck, Card}
import com.glyph.scala.lib.util.updatable.{Updatables, UpdateQueue}
import com.badlogic.gdx.scenes.scene2d.actions.{MoveToAction, Actions}
import com.badlogic.gdx.math.Interpolation
import com.glyph.scala.lib.util.collection.list.DoubleLinkedQueue

/**
 * @author glyph
 */
class CardViewController(view: CardView, game: Game, deck: Deck) extends Updatables {
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
    val token = new CardToken(view.getWidth/5,view.getHeight)
    token.onPressed = (x, y) => {
      token.explode {
        token.remove()
        tokens.remove(token)
        game.puzzle.scan()
        setupTokenPosition()
        game.drawCard()
      }
      //TODO remove from hands
    }
    tokens.enqueue(token)
    if (tokens.isEmpty) {
      view.addActor(token)
    } else {
      view.addActorAfter(tokens.last, token)
    }
  }

  def setupTokenPosition() {
    import Actions._
    var i = 0
    tokens foreach {
      token => {
        val move = action(classOf[MoveToAction])
        move.setPosition((view.getWidth / 5) * i, 0)
        move.setDuration(0.5f)
        move.setInterpolation(Interpolation.exp10Out)
        //token.clearActions()
        token.addAction(move)
        i += 1
      }
    }
  }
}
