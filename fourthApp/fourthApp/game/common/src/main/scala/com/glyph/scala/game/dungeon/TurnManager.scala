package com.glyph.scala.game.dungeon

import collection.mutable.ListBuffer
import collection.mutable
import com.glyph.scala.Glyph
import com.glyph.scala.lib.util.callback.Callback

/**
 * should turn manager know the animation manager? obviously no.
 * this class do not know of any other classes...except the TurnProcessor
 * @author glyph
 */
class TurnManager{
  val onReactionDone = new Callback
  val onMoveDone = new Callback
  val onActionDone = new Callback
  val onFocusActionDone = new Callback
  val onFocusMoveDone = new Callback

  private var focus:TurnProcessor = null
  val processors = ListBuffer[TurnProcessor]()
  private var state:State = Focus

  private val log = Glyph.log("TurnManager")_

  def setFocus(f:TurnProcessor){
    if(focus != null){
      focus.onActionEnd -= onActionEnd
      focus.onMoveEnd -= onMoveEnd
    }
    focus = f
    focus.onActionEnd += onActionEnd
    focus.onMoveEnd += onMoveEnd
  }
  private def onActionEnd(){
    state.onActionEnd()
  }
  private def onMoveEnd(){
    state.onMoveEnd()
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
    Reaction.enqueueReaction(p)
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
  trait State{
    def onMoveEnd(){}
    def onActionEnd(){}
    def onEnter(){log("enter:"+this.getClass.getSimpleName)}
    def onPhaseEnd(){log("phaseEnd:"+this.getClass.getSimpleName)}
    def onExit(){}
  }

  /**
   * プレイヤー行動待ち状態
   */
  object Focus extends State{

    override def onMoveEnd(){
      onFocusMoveDone()
    }
    override def onActionEnd(){
      onFocusActionDone()
    }

    override def onPhaseEnd() {
      super.onPhaseEnd()
      setState(Reaction)
    }
  }

  /**
   * 反応処理
   */
  object Reaction extends State{
    var reactions = mutable.Queue[TurnProcessor]()
    def enqueueReaction(p:TurnProcessor){reactions.enqueue(p)}
    override def onEnter() {
      super.onEnter()
      if(!reactions.isEmpty){
        reactions.foreach{_.onActionPhase()}
        onReactionDone()
      }else{//skip if there is no reaction
        setState (Move)
      }
    }

    override def onPhaseEnd() {
      super.onPhaseEnd()
      setState (Move)
    }
  }

  /**
   * 移動処理
   */
  object Move extends State{
    override def onEnter() {
      super.onEnter()
      processors.sortBy(focus.getPosition() - _.getPosition()).foreach {_.onMovePhase()}
      onMoveDone()
    }
    override def onPhaseEnd() {
      super.onPhaseEnd()
      setState(Action)
    }

  }
  /**
   * 行動処理
   */
  object Action extends State{
    override def onEnter() {
      super.onEnter()
      processors.sortBy(p=>focus.getPosition() - p.getPosition()).foreach {_.onActionPhase()}
      onActionDone()
    }
    override def onPhaseEnd() {
      super.onPhaseEnd()
      setState(Focus)
    }
  }
}