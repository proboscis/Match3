package com.glyph.scala.game.puzzle.controller

import com.glyph.scala.game.puzzle.model.Game
import com.glyph.scala.game.puzzle.model.cards.{Card, Meteor, Scanner}
import com.badlogic.gdx.math.MathUtils
import com.glyph.scala.lib.util.reactive
import reactive.{Var, EventSource}

/**
 * Receives events from view, and pass it to the game model
 * this is like an activity of the androids!
 * @author glyph
 */
class PuzzleGameController(game: Game) {
  //TODO slide in out
  import game._

  /**
   * notified when the state of this object is changed
   */
  val state = Var[State](Idle)
  val stateChange = state.toEvents

  def startScanSequence() {
    state().handle(StartScan)
  }

  def damage() {
    game.player.hp() = game.player.hp() - 10
  }

  def destroy(request: (Int, Int)*) {
    puzzle.removeIndices(request: _*)
    state().handle(Destroy)
  }

  val cardSeed = Array(
    () => new Scanner,
    () => new Meteor
  )

  def initialize() {
    (1 to 40) foreach {
      _ =>
        deck.addCard(cardSeed(MathUtils.random(1))())
    }
    (1 to 5) foreach {
      _ => deck.drawCard()
    }
    puzzle.fill(puzzle.createFilling)
  }

  def drawCard() {
    deck.drawCard()
  }

  def discard(card: Card) {
    deck.discard(card)
  }

  trait Event

  object Destroy extends Event

  object StartScan extends Event

  object RemoveAnimationEnd extends Event

  object FillAnimationEnd extends Event


  def onRemoveAnimationEnd() {
    state().handle(RemoveAnimationEnd)
  }

  def onFillAnimationEnd() {
    state().handle(FillAnimationEnd)
  }

  // i want to define only related behaviors... well, define the default behavior...
  trait State {
    def handle(e: Event) {
      //println("handle:" + e)
    } //do nothing by default
  }

  object Idle extends State {
    override def handle(e: Event) {
      super.handle(e)
      e match {
        case StartScan => {
          val scanned = puzzle.scan()
          if (!scanned.isEmpty) {
            state() = Animating
            puzzle.remove(scanned)
          }
        }
        case Destroy => {
          val filling = puzzle.createFilling
          if (!filling.isEmpty) {
            state() = Animating
            puzzle.fill(filling)
          }
        }
        case _ =>
      }
    }
  }

  object Animating extends State {
    override def handle(e: Event) {
      super.handle(e)
      e match {
        case RemoveAnimationEnd => {
          puzzle.fill(puzzle.createFilling)
        }
        case FillAnimationEnd => {
          val scanned = puzzle.scan()
          if (!scanned.isEmpty) {
            puzzle.remove(scanned)
          } else {
            state() = Idle
          }
        }
        case _ =>
      }
    }
  }

}
