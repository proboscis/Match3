package com.glyph.scala.game.dungeon.animation

import com.glyph.scala.lib.util.update.Updatable
import com.glyph.scala.lib.util.LinkedList
import com.glyph.scala.Glyph
import com.glyph.scala.lib.util.callback.Callback

/**
 * @author glyph
 */
class AnimationManager extends  Updatable{
  val onSequenceEnd = new Callback
  val onParallelEnd = new Callback

  private val animationQueue = new LinkedList[Animation]()
  private var state :State= Idle
  private val log = Glyph.log("AnimationManager")_
  def postAnimation(ani:Animation){
    log("post"+ani)
    animationQueue push ani
  }

  /**
   * starts all the added animation as sequential animation
   */
  def startSequential(){
    state.startSequential()
  }

  /**
   * starts all the added animation as parallel animation
   */
  def startParallel(){
    state.startParallel()
  }

  /**
   * update all the animations
   * @param delta
   */
  def update(delta:Float){
    state.update(delta)
  }

  /**
   * call this when you finished animating
   * @param ani
   */
  def animationEnd(ani:Animation){
    state.onAnimationEnd()
  }

  /**
   * use this method to change the state
   * @param state
   */
  private def setState(state:State){
    this.state = state
    state.enter()
  }

  trait State extends Updatable{
    def enter(){}
    def update(delta: Float) {}
    def startSequential(){}
    def startParallel(){}
    def onAnimationEnd(){}
  }

  /**
   * state that represents nothing is being done
   */
  object Idle extends State{
    override def startSequential() {log("startSequential");setState(Sequential)}
    override def startParallel() {log("startParallel");setState(Parallel)}
  }

  /**
   * state that represents Sequential animation is in progress
   */
  object Sequential extends State{
    var current:Animation = null
    def proceed(){
      if (animationQueue.isEmpty){
        setState(Idle)
        onSequenceEnd()
      }else{
        current = animationQueue.pop()
      }
    }

    override def enter() {
      super.enter()
      proceed()
    }

    override def update(delta: Float) {
      super.update(delta)
      log("sequential update"+animationQueue.size)
      current.update(delta)
    }

    override def onAnimationEnd() {
      super.onAnimationEnd()
      proceed()
    }
  }

  /**
   * Parallel animation
   */
  object Parallel extends State{
    var count = 0
    override def enter() {
      super.enter()
      count = animationQueue.size
      if (count > 0){
        animationQueue.foreach(_.start())
      }else{
        setState(Idle)
        onParallelEnd()
      }
    }

    override def update(delta: Float) {
      super.update(delta)
      animationQueue.foreach(_.update(delta))
    }

    override def onAnimationEnd() {
      super.onAnimationEnd()
      count -= 1
      if (count == 0){
        animationQueue.clear()
        setState(Idle)
        onParallelEnd()
      }
    }
  }
}