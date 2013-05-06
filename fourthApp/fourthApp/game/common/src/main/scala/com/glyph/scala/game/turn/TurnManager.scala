package com.glyph.scala.game.turn

import collection.mutable.ListBuffer

/**
 * @author glyph
 */
class TurnManager{
  private val dummy = new Dummy
  val processors = ListBuffer[TurnProcessor]()
  var state:AbstractState = null

  def onTurnEnd(p:TurnProcessor){
    state.onTurnEnd(p)
  }

  def start(){
    state.start()
  }
  def setState(state:AbstractState){
    this.state = state
  }

  private class Dummy extends TurnProcessor{
    def startTurn(state: AbstractState) {

    }
  }
}
