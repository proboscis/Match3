package com.glyph.scala.game.puzzle.view

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.glyph.scala.game.puzzle.model.PlayableDeck
import com.glyph.scala.lib.libgdx.actor.Updating
import com.glyph.scala.lib.util.updatable.UpdateQueue
import com.badlogic.gdx.scenes.scene2d.actions.{MoveToAction, Actions}
import com.badlogic.gdx.math.{MathUtils, Interpolation}
import com.glyph.scala.game.puzzle.model.cards.Card
import com.glyph.scala.lib.util.reactive.{Var, Reactor, EventSource}
import com.badlogic.gdx.scenes.scene2d.{Touchable, Group, InputEvent, InputListener}
import com.glyph.scala.lib.util.Logging
import com.glyph.scala.game.puzzle.controller.PuzzleGameController
import com.badlogic.gdx.assets.AssetManager

/**
 * @author glyph
 */
class CardTableView[T](assets:AssetManager,deck: PlayableDeck[T]) extends Table with Updating with Reactor with Logging {
  type PCard = Card[T]#PlayableCard
  val updateQueue = new UpdateQueue(0.1f)
  this.add(updateQueue)
  @deprecated //never emits anything...
  val cardPress = new EventSource[CardToken[PuzzleGameController]]
  val tokens = scala.collection.mutable.ListBuffer[Offset]()
  val checkingSwipe = Var(false,"checkingSwipe")

  def cardW: Float = getWidth / 5 * 0.9f

  def cardH: Float = getHeight * 0.9f

  def marginX: Float = (getWidth - cardW * deck.hand().size) / (deck.hand().size + 1)

  def marginY: Float = (getHeight - cardH) / 2

  reactEvent(deck.drawCardEvent) {
    card =>
      createToken(card)
      updateQueue.enqueue {
        setupTokenPosition()
      }
  }
  reactEvent(deck.discardEvent) {
    card =>
      updateQueue.enqueue {
        tokens.find {
          case Offset(token) => token.card eq card
        }.foreach {
          case offset@Offset(token) => token.explode {
            offset.remove()
            println("discard:" + token.card.source)
            tokens -= offset
            offset.dispose()
            setupTokenPosition()
          }
        }
      }
  }

  def createToken(card: PCard) {
    log("createToken:" + card.source)
    val token = new CardToken(assets,card, cardW, cardH,checkingSwipe)
    val offset = Offset(token)
    offset.setPosition(marginX + getWidth, marginY)
    tokens += offset
    if (tokens.isEmpty) {
      addActor(offset)
    } else {
      addActorAfter(tokens.last, offset)
    }
  }

  def setupTokenPosition() {
    import Actions._
    var i = 0
    tokens foreach {
      token => {
        val move = action(classOf[MoveToAction])
        move.setPosition(marginX + (cardW + marginX) * i, marginY)
        move.setDuration(0.5f)
        move.setInterpolation(Interpolation.exp10Out)
        //token.clearActions()
        token.addAction(move)
        i += 1
      }
    }
  }
  def startSwipeCheck(cb: CardToken[T] => Unit) {
    //TODO previous card is still here and blocking the touch!!!
    log("startSwipeCheck:" + tokens)
    if(checkingSwipe()) error("swipe check is already started, or previous check is not finished correctly.")
    checkingSwipe() = true
    tokens foreach {
      case offsetToken@Offset(cardToken) => //log("matched")
        //cardToken.setTouchable(Touchable.disabled)
        //offsetToken.setTouchable(Touchable.enabled)

        /**
        offsetToken.addListener(new ActorGestureListener() {

          override def fling(event: InputEvent, velocityX: Float, velocityY: Float, button: Int) {
            super.fling(event, velocityX, velocityY, button)
            log("fling:" +(event, velocityX, velocityY, button))
            used = true
            cb(cardToken)
          }
        })
          * */
        offsetToken.addListener(new InputListener() {
          var touchX, touchY = 0f
          var startX, startY = 0f

          override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = {
            touchX = x
            touchY = y
            startX = cardToken.getX
            startY = cardToken.getY
            log("down:" +(startX, startY))
            true
          }

          override def touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) {
            super.touchDragged(event, x, y, pointer)
            log("touchDragged" +(x, y))
            cardToken.setY(MathUtils.clamp(y - touchY, 0, 50))
          }

          override def touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
            super.touchUp(event, x, y, pointer, button)
            if (y - touchY > 50) {
              cb(cardToken)
            } else {
              cardToken.addAction(Actions.moveTo(startX, startY, 0.2f, Interpolation.exp10Out))
            }
            log("up")
          }
        })
    }
  }

  def stopSwipeCheck() {
    tokens foreach {
      _.clearListeners()
    }
    checkingSwipe() = false
  }
  case class Offset(token: CardToken[T]) extends Group with Reactor with Logging{
    log("created offset:"+token)
    this.addActor(token)
    setSize(token.getWidth, token.getHeight)
    import com.glyph.scala.lib.util.reactive.~
    val reactor = reactVar(token.card.playable~checkingSwipe){
      case playable~swiping =>
        //log("play,swipe=>"+(playable,swiping))
        if(playable && swiping){
        this.setTouchable(Touchable.enabled)
      } else{
        this.setTouchable(Touchable.disabled)
      }
    }
    def dispose(){
      reactor.unSubscribe()
      token.dispose()
    }
  }
}


