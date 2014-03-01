package com.glyph._scala.lib.libgdx.actor.transition

import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager.AnimatedConstructor
import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph._scala.lib.util.Animated
import com.glyph._scala.lib.util.reactive.{Reactor, Varying}

class VaryingAnimatedConstructorHolder(target:Varying[AnimatedConstructor]) extends AnimatedConstructor {
  override def apply(info: AnimatedManager.Info): (AnimatedManager.Callbacks) => Actor with Animated = callbacks =>  new AnimatedActorHolder
    with Animated
    with Reactor {
    var current: Option[Actor with Animated] = None
    import scalaz._
    import Scalaz._
    reactVar(target) {
      animated =>
        current match {
          case Some(prev)=>
            out(prev)(() => {})
            current = Some(animated(info)(callbacks) <| (in(_)(()=>{})) )
          case None=>
            current = Some(animated(info)(callbacks))
        }
    }

    override def in(cb: () => Unit): Unit = {
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
  def apply(target: Varying[AnimatedConstructor]): AnimatedConstructor = new VaryingAnimatedConstructorHolder(target)
}

