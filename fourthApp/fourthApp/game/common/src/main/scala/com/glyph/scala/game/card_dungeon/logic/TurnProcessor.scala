package com.glyph.scala.game.logic
import com.glyph.scala.lib.util.callback.{Callback, Callbacks}

/**
 * @author glyph
 */
trait TurnProcessor{
  var idle:State = new Idle
  var action:State = new Action
  val onTurnEnd = new Callback
  private var state_ :State = idle
  def state = state_
  protected def state_=(s:State){
    state_ = s
    state_.onEnter()
  }
  /**
   * call manager's turnEnd function when you finished
   * beware of recursive calls.
   */
  def beginTurn(){
    state = action
  }
  def endTurn(){
    state = idle
    onTurnEnd()
  }

  trait State{
    val onEnterCallback = new Callbacks
    def onEnter(){}
  }
  class Idle extends State
  class Action extends State
}
