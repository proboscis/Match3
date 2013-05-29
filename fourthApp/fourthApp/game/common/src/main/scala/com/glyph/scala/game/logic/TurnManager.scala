package com.glyph.scala.game.logic

import collection.mutable.ListBuffer
import collection.mutable

/**
 * TurnProcessorのdelta関数を呼ぶだけのクラスになるか？
 * @author glyph
 */
class TurnManager {
  val processors = ListBuffer[TurnProcessor]()
  private var state_ : State = Idle

  def add(processor:TurnProcessor){
    processors += processor
  }
  def remove(processor:TurnProcessor){
    processors -= processor
  }

  def state = state_
  def state_=(s: State) {
    state_.onExit()
    state_ = s
    state_.onEnter()
  }

  def startCycle() {
    state.startCycle()
  }
  def endCycle(){
    state = Idle
  }

  trait State {
    def startCycle() {}
    def onEnter() {}
    def onExit(){}
  }

  object Idle extends State {

    override def startCycle() {
      super.startCycle()
      state = Processing
    }
  }

  object Processing extends State {
    val queue = new mutable.Queue[TurnProcessor]
    var current: TurnProcessor = null

    override def onEnter() {
      super.onEnter()
      clear()
      processors.foreach{queue+=_}
      process()
    }

    def process() {
      if (current != null) current.onTurnEnd {}
      if (!queue.isEmpty) {
        current = queue.dequeue()
        current.onTurnEnd {
          process()
        }
        current.beginTurn()
      } else {
        processors.foreach{queue+=_}
        process()
      }
    }
    def clear(){
      queue.clear()
      processors.foreach {_.onTurnEnd{}}
    }

    override def onExit() {
      super.onExit()
      clear()
    }
  }
}
