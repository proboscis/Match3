package com.glyph.scala.game.puzzle.view

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.glyph.scala.game.puzzle.model.Deck
import com.glyph.scala.lib.libgdx.actor.Updating
import com.glyph.scala.lib.util.updatable.UpdateQueue
import com.glyph.scala.lib.util.observer.Observing
import com.glyph.scala.lib.util.collection.list.DoubleLinkedQueue
import com.badlogic.gdx.scenes.scene2d.actions.{MoveToAction, Actions}
import com.badlogic.gdx.math.Interpolation
import com.glyph.scala.game.puzzle.model.cards.Card
import com.glyph.scala.lib.util.reactive.{Reactor, EventSource}

/**
 * @author glyph
 */
class CardTableView(deck: Deck) extends Table with Updating with Observing with Reactor {
  val updateQueue = new UpdateQueue(0.1f)
  this.add(updateQueue)
  val cardPress = new EventSource[CardToken]
  val tokens = new DoubleLinkedQueue[CardToken]

  def removeToken(token: CardToken) {
    token.explode {
      token.remove()
      tokens.remove(token)
      setupTokenPosition()
      removeObserver(token.press)
    }
  }
  reactEvent(deck.drawCardEvent) {
    card => updateQueue.enqueue {
      createToken(card)
      setupTokenPosition()
    }
  }
  reactEvent(deck.discardEvent) {
    card =>
      updateQueue.enqueue {
        tokens.find {
          token => token.card eq card
        }.foreach {
          token => token.explode {
            token.remove()
            tokens.remove(token)
            setupTokenPosition()
          }
        }
      }
  }

  def createToken(card: Card) {
    val token = new CardToken(card, getWidth / 5, getHeight)
    observe(token.press) {
      pos =>
        cardPress.emit(token) //TOOD イベントの結合
    }
    tokens.enqueue(token)
    if (tokens.isEmpty) {
      addActor(token)
    } else {
      addActorAfter(tokens.last, token)
    }
  }

  def setupTokenPosition() {
    import Actions._
    var i = 0
    tokens foreach {
      token => {
        val move = action(classOf[MoveToAction])
        move.setPosition((getWidth / 5) * i, 0)
        move.setDuration(0.5f)
        move.setInterpolation(Interpolation.exp10Out)
        //token.clearActions()
        token.addAction(move)
        i += 1
      }
    }
  }
}
