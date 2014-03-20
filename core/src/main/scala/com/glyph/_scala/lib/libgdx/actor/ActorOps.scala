package com.glyph._scala.lib.libgdx.actor

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.math.Rectangle
import com.glyph._scala.lib.libgdx.actor.ActorOps.ActorOpsImpl

/**
 * @author glyph
 */
trait ActorOps {
  implicit def actorToActorOps(actor:Actor):ActorOpsImpl = new ActorOpsImpl(actor)
  implicit def actorIsRectangle(actor:Actor):Rectangle = actor.bounds
}
object ActorOps extends ActorOps{
  implicit class ActorOpsImpl(val actor:Actor) extends AnyVal{
    def bounds:Rectangle = new Rectangle(actor.getX,actor.getY,actor.getWidth,actor.getHeight)
  }
}
