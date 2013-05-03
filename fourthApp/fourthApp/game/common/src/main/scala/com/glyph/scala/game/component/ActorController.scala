package com.glyph.scala.game.component

import com.glyph.scala.lib.engine.Entity
import com.glyph.scala.game.component.Transform
import com.glyph.scala.game.GameContext
import com.glyph.scala.game.event.UIInputEvent
import com.glyph.scala.lib.util.Disposable

/**
 * @author glyph
 */
class ActorController(owner :Entity,context:GameContext) extends Disposable{
  lazy val transform = owner.get[Transform]
  context.eventManager += onInput
  def onInput(e:UIInputEvent):Boolean={
    e.typ match {
      case UIInputEvent.RIGHT_BUTTON => transform.position.x += 1
      case UIInputEvent.LEFT_BUTTON => transform.position.x -= 1
      case UIInputEvent.EXEC_BUTTON => transform.position.y += 1
      case _ =>
    }
    false
  }

  def dispose() {
    context.eventManager -= onInput
  }
}
