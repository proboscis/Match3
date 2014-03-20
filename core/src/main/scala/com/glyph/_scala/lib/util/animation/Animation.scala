package com.glyph._scala.lib.util.animation

import scala.concurrent.ExecutionContext
import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph._scala.lib.util.animation.Animation.AnimationConstructor
import com.badlogic.gdx.math.Rectangle
import org.omg.CORBA.Any
import com.glyph._scala.lib.util.animation.Adapter.{Info, LayoutAnimationConstructor}

case class LayoutInfo(bounds:Rectangle,option:Info,target:Actor)
object Adapter{
  type Info = String Map Any
  type Layout = Seq[LayoutInfo]
  type LayoutAnimationConstructor = Rectangle => Layout => Animation
  implicit class LayoutOps(val layout:Layout) extends AnyVal{
    def actors = layout.map{
      case LayoutInfo(_,_,actor)=>actor
    }
  }
}

/**
 * is animation reusable? no,
 * but animation constructor is.
 * should animation have input and output?
 */
trait Animation{
  /**
   * @return true while animating
   */
  def isAnimating: Boolean

  /**
   * starts animations
   * @return true if successfully started else false
   */
  def start(onFinish: () => Unit): Boolean

  /**
   * cancels this animation
   * @return true if successfully canceled else false
   */
  def cancel(): Boolean
}

object Animation {
  type AnimationConstructor[P] = P=>Animation
}

class Sequence(animations: Seq[Animation]) extends (Animation) {
  private val animationQueue = collection.mutable.Queue[Animation]()
  private var currentAnimation: Animation = null

  /**
   * @return true while animating
   */
  override def isAnimating: Boolean = currentAnimation != null

  /**
   * starts animations
   * @return true if successfully started else false
   */
  override def start(onFinish: () => Unit): Boolean = {
    assert(!isAnimating)
    animationQueue.clear()
    animationQueue.enqueue(animations: _*)
    def startNext() {
      if (!animationQueue.isEmpty) {
        currentAnimation = animationQueue.dequeue()
        currentAnimation.start(() => {
          startNext()
        })
      } else {
        currentAnimation = null
        onFinish()
      }
    }
    startNext()
    true
  }

  /**
   * cancels this animation
   * @return true if successfully canceled else false
   */
  override def cancel(): Boolean = {
    if (currentAnimation != null) {
      currentAnimation.cancel()
      currentAnimation = null
    }
    true
  }
}

object Sequence {
  def main(args: Array[String]) {
    import ExecutionContext.Implicits.global
    new Sequence(new Invoke(() => {
      println("hello")
    }) :: new Invoke(() => {
      println("world")
    }) :: Nil).start(() => {
      println("finished")
    })
    Thread.currentThread().join(19000)
  }
}

class Invoke(f: () => Unit)(implicit context: ExecutionContext) extends (Animation) {

  private var postFunction: Cancellable = null

  /**
   * @return true while animating
   */
  override def isAnimating: Boolean = postFunction != null

  /**
   * starts animations
   * @return true if successfully started else false
   */
  override def start(onFinish: () => Unit): Boolean = {
    assert(!isAnimating)
    postFunction = new Cancellable(() => {
      f()
      onFinish()
      postFunction = null
    })
    context.prepare().execute(new Runnable {
      val closure = postFunction

      //this must be done since the postFunction will change after this invocation
      override def run(): Unit = closure()
    })
    true
  }

  /**
   * cancels this animation
   * @return true if successfully canceled else false
   */
  override def cancel(): Boolean = {
    if (postFunction == null) {
      postFunction.cancel()
    }
    true
  }
}

class Cancellable(f: () => Unit) extends (() => Unit) {
  private var canceled = false

  override def apply(): Unit = if (!canceled) f()

  def cancel() {
    canceled = true
  }
}

/*
class Parallel extends Animation
class Do extends Animation
*/
