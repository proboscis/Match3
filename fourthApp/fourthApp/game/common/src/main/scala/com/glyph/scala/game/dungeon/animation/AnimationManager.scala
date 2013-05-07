package com.glyph.scala.game.dungeon.animation

import com.glyph.scala.lib.util.observer.Observable
import com.glyph.scala.lib.util.update.Updatable
import com.glyph.scala.lib.util.LinkedList

/**
 * @author glyph
 */
class AnimationManager extends Observable[AnimationManager] with Updatable{
  val animationQueue = new LinkedList[Animation]()
  val IDLE = new Idle
  val SEQUENTIAL = new Sequential
  val PARALLEL = new Parallel
  var state :State= IDLE
  //TODO create sequential and parallel animation
  def postAnimation(ani:Animation){
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
    override def startSequential() {}
    override def startParallel() {}
  }

  /**
   * state that represents Sequential animation is in progress
   */
  class Sequential extends State{
    var current:Animation = null
    def proceed(){
      if (animationQueue.isEmpty){
        setState(IDLE)
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
      animationQueue.foreach(_.start())
    }

    override def update(delta: Float) {
      super.update(delta)
      animationQueue.foreach(_.update(delta))
    }

    override def onAnimationEnd() {
      super.onAnimationEnd()
      count -= 1
      if (count == 0){
        setState(IDLE)
      }
    }
  }
}
