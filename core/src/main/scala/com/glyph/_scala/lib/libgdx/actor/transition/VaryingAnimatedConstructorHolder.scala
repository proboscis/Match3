package com.glyph._scala.lib.libgdx.actor.transition

import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager.AnimatedConstructor
import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph._scala.lib.util.Animated
import com.glyph._scala.lib.util.reactive.{Reactor, Varying}
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedConstructorOps.ACG
import scalaz._
import Scalaz._

import com.glyph._scala.game.Glyphs
import Glyphs._
class VaryingAnimatedConstructorHolder[A<:AnimatedConstructor](target:Varying[A]) extends AnimatedConstructor {
  override def apply(info: AnimatedManager.Info): (AnimatedManager.Callbacks) => Actor with Animated = callbacks =>  new AnimatedActorHolder
    with Animated
    with Reactor {
    var current: Option[Actor with Animated] = None
    var first = false

    reactVar(target) {
      animated =>
        current match {
          case Some(prev)=>
            out(prev)(() => {})
            current = Some(animated(info)(callbacks) <| (anim =>{
              err("the varying's value is changed.:"+animated)
              in(anim)(()=>{
                err("varied one is now in:"+anim.hashString)
              })})
            )
          case None=>
            current = Some(animated(info)(callbacks))
            //you do not have call in since it will be called by the animated holder
        }
    }

    override def in(cb: () => Unit): Unit = {
      err("in!")
      current foreach (in(_)(cb))
    }

    override def out(cb: () => Unit): Unit = {
      current foreach (out(_)(cb))
    }

    override def pause(cb: () => Unit): Unit = {
      current foreach (pause(_)(cb))
    }

    override def resume(cb: () => Unit): Unit = {
      current foreach(resume(_)(cb))
    }
  }
}

/**
 * @author glyph
 */
object VaryingAnimatedConstructorHolder {
  def apply[A<:AnimatedConstructor](target: Varying[A]): AnimatedConstructor = new VaryingAnimatedConstructorHolder(target)
}

