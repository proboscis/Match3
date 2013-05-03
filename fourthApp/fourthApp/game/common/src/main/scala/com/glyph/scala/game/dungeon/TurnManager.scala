package com.glyph.scala.game.dungeon

import collection.mutable.ListBuffer
import collection.mutable

/**
 * @author glyph
 */
class TurnManager {
  self =>
  private var focus:TurnProcessor = null
  val processors = ListBuffer[TurnProcessor]()
  private val REACTION = new Reaction
  private val ACTION = new Action
  private val IDLE = new Idle
  private val MOVE = new Move
  var state:State = IDLE

  def setFocus(f:TurnProcessor){
    focus = f
  }

  def start() {
    /**
     * まずは反応処理、
     * 次に移動処理
     * 次に行動処理...
     */
    setState(REACTION)
  }


  private def setState(s:State){
    state.onExit()
    state = s
    state.onEnter()
  }

  def turnEnd(p:TurnProcessor){
    state.onTurnEnd(p)
  }

  def enqueueReaction(p:TurnProcessor){
    REACTION.enqueueReaction(p)
  }
  def addProcessor(p:TurnProcessor){
    processors += p
  }
  def removeProcessor(p:TurnProcessor){
    processors -= p
  }

  /**
   * State classes
   */
  class State{
    def onEnter(){}
    def onTurnEnd(p:TurnProcessor){}
    def onExit(){}
  }

  /**
   * 何も起きない状態
   */
  class Idle extends State

  /**
   * 反応処理
   */
  class Reaction extends State{
    var reactions = mutable.Queue[TurnProcessor]()
    def enqueueReaction(p:TurnProcessor){reactions.enqueue(p)}
    override def onEnter() {
      super.onEnter()
      reactions = reactions.sortBy(p=>focus.getPosition() - p.getPosition())
      reactions.dequeue().onActionPhase(self)
    }
    override def onTurnEnd(p: TurnProcessor) {
      if (reactions.isEmpty){
        setState(MOVE)
      }else{
        reactions.dequeue().onActionPhase(self)
      }
    }
  }

  /**
   * 移動処理
   */
  class Move extends State{
    def moves = mutable.Queue[TurnProcessor]()
    var count = 0
    override def onEnter() {
      super.onEnter()
      moves ++= processors.sortBy(focus.getPosition() - _.getPosition())
      moves.foreach{
        p=> p.onMovePhase(self)
        count += 1
      }
    }
    override def onTurnEnd(p: TurnProcessor) {
      super.onTurnEnd(p)
      count -= 1
      if (count < 0){
        throw new RuntimeException("count < 0 is impossible since it means someone hase called onTurnEnd more than once ")
      }else if (count == 0){
        setState(ACTION)
      }
    }
  }

  /**
   * 行動処理
   */
  class Action extends State{
    var actions = mutable.Queue[TurnProcessor]()
    def process(){
      if (actions.isEmpty){
        setState(IDLE)
      }else{
        actions.dequeue().onActionPhase(self)
      }
    }
    override def onEnter() {
      super.onEnter()
      actions ++= processors.sortBy(p=>focus.getPosition() - p.getPosition())
      process()
    }
    override def onTurnEnd(p: TurnProcessor) {
      super.onTurnEnd(p)
      process()
    }
  }

}
