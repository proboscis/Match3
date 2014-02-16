package com.glyph._scala.lib.libgdx.actor.action

import com.badlogic.gdx.scenes.scene2d.{Actor, Action}
import collection.mutable
import com.glyph._scala.lib.util.callback.Callback

/**
 * @author glyph
 */
class Waiter{
  val waiting = mutable.HashMap[Action,Boolean]()
  val onComplete = new Callback
  def await():Action={
    val act = new Action {
      def act(p1: Float): Boolean ={
        complete(this)
        true
      }

      override def setActor(actor: Actor) {
        super.setActor(actor)
        if (actor == null){
          cancel(this)
        }
      }
    }
    waiting(act) = false
    act
  }
  private def complete(a:Action){
    waiting(a) = true
    if(waiting.forall(_._2)){onComplete()}
  }
  private def cancel(a:Action){
    waiting.remove(a)
  }
}
object Waiter{
  def apply(f: =>Unit):Waiter = {
    val w = new Waiter
    w.onComplete(f)
    w
  }
}
