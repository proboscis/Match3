package com.glyph.scala.game.puzzle.controller

import com.glyph.scala.game.puzzle.model.Game
import com.glyph.scala.lib.util.observer.Observing
import com.glyph.scala.lib.util.observer.reactive.{Reactor, Var, EventSource}

/**
 * Receives events from view, and pass it to the game model
 * this is like an activity of the androids!
 * @author glyph
 */
class PuzzleGameController(game: Game) extends Observing with Reactor{
  //TODO slide in out

  import game._

  /**
   * notified when the state of this object is changed
   */
  val state = new Var[State](Idle){

    override def subscribe(callback: (PuzzleGameController.this.type#State) => Unit) {
      super.subscribe(callback)
      println("sub=>"+reactiveObservers)
    }

    override def unSubscribe(callback: (PuzzleGameController.this.type#State) => Unit) {
      super.unSubscribe(callback)
      println("unsub=>"+reactiveObservers)
    }

    override def notifyObservers(t: PuzzleGameController.this.type#State) {
      super.notifyObservers(t)
      println("notify=>"+reactiveObservers)
    }
  }

  react(state){
    s => println("react=>"+s)
  }

  trait Event

  object StartScan extends Event

  object RemoveAnimationEnd extends Event

  object FillAnimationEnd extends Event

  def startScanSequence() {
    state().handle(StartScan)
  }
  def damage(){
    game.player.hp() = game.player.hp()-100
  }

  def onRemoveAnimationEnd() {
    state().handle(RemoveAnimationEnd)
  }

  def onFillAnimationEnd() {
    state().handle(FillAnimationEnd)
  }

  // i want to define only related behaviors... well, define the default behavior...
  trait State {
    def handle(e: Event) {
      println("handle:" + e)
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
            println("emit Idle")
            state() = Idle//TODO Idleが通知されない問題を解決
          }
        }
        case _ =>
      }
    }
  }

}
