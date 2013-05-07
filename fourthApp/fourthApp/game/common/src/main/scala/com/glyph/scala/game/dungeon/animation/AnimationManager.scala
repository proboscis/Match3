package com.glyph.scala.game.dungeon.animation

import com.glyph.scala.lib.util.update.Updatable
import com.glyph.scala.lib.util.LinkedList
import com.glyph.scala.lib.util.callback.Callback
import com.glyph.scala.Glyph

/**
 * @author glyph
 */
class AnimationManager extends Callback with Updatable{
  import AnimationManager._
  val animationQueue = new LinkedList[Animation]()
  val IDLE = new Idle
  val SEQUENTIAL = new Sequential
  val PARALLEL = new Parallel
  var state :State= IDLE
  val log = Glyph.log("AnimationManager")_
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

  class State extends Updatable{
    def enter(){}
    def update(delta: Float) {}
    def startSequential(){}
    def startParallel(){}
    def onAnimationEnd(){}
  }

  /**
   * state that represents nothing is being done
   */
  class Idle extends State{
    override def startSequential() {log("startSequential");setState(SEQUENTIAL)}
    override def startParallel() {log("startParallel");setState(PARALLEL)}
  }

  /**
   * state that represents Sequential animation is in progress
   */
  class Sequential extends State{
    var current:Animation = null
    def proceed(){
      if (animationQueue.isEmpty){
        setState(IDLE)
        callback(SEQUENTIAL_DONE)
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
  class Parallel extends State{
    var count = 0
    override def enter() {
      super.enter()
      count = animationQueue.size
      if (count > 0){
        animationQueue.foreach(_.start())
      }else{
        setState(IDLE)
        callback(PARALLEL_DONE)
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
        setState(IDLE)
        callback(PARALLEL_DONE)
      }
    }
  }
}
object AnimationManager{
  final val SEQUENTIAL_DONE = 0
  final val PARALLEL_DONE = 1
}
