package com.glyph.scala.game.dungeon

import collection.mutable.ListBuffer
import collection.mutable
import com.glyph.scala.Glyph
import com.glyph.scala.lib.util.observer.Observable

/**
 * should turn manager know the animation manager? obviously no.
 * this class do not know of any other classes...except the TurnProcessor
 * @author glyph
 */
class TurnManager extends Observable[TurnManager]{
  //TODO let the animation manager know the time to start animation
  self =>
  private var focus:TurnProcessor = null
  val processors = ListBuffer[TurnProcessor]()
  private val REACTION = new Reaction
  private val ACTION = new Action
  private val IDLE = new Idle
  private val MOVE = new Move
  var state:State = IDLE

  val log = Glyph.log("TurnManager")_

  def setFocus(f:TurnProcessor){
    focus = f
  }

  def start() {
    /**
     * まずは反応処理、
     * 次に移動処理
     * 次に行動処理...
     */
    log("start")
    setState(REACTION)
  }

  private def setState(s:State){
    log("set State:"+s.getClass.getSimpleName)
    state.onExit()
    state = s
    state.onEnter()
  }

  /**
   * call this method when all the procedures are finished in order to proceed to next phase
   */
  def phaseEnd(){
    state.onPhaseEnd()
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
    def onEnter(){log("enter:"+this.getClass.getSimpleName)}
    def onPhaseEnd(){}
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
      reactions.foreach{_.onActionPhase()}
      //TODO let animation manager start
    }

    override def onPhaseEnd() {
      super.onPhaseEnd()
      setState (MOVE)
    }
  }

  /**
   * 移動処理
   */
  class Move extends State{
    override def onEnter() {
      super.onEnter()
      processors.sortBy(focus.getPosition() - _.getPosition()).foreach {_.onMovePhase()}
      //TODO let the animation manager do its work
    }
    override def onPhaseEnd() {
      super.onPhaseEnd()
      setState(ACTION)
    }
  }
  /**
   * 行動処理
   */
  class Action extends State{
    override def onEnter() {
      super.onEnter()
      processors.sortBy(p=>focus.getPosition() - p.getPosition()).foreach {_.onActionPhase()}
      //TODO let the animation manager do its work
    }
    override def onPhaseEnd() {
      super.onPhaseEnd()
      setState(IDLE)
    }
  }

}
