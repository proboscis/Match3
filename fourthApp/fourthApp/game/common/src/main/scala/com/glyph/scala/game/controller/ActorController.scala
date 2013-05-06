package com.glyph.scala.game.component.controller

import com.glyph.scala.lib.engine.Entity
import com.glyph.scala.game.GameContext
import com.glyph.scala.game.event.UIInputEvent
import com.glyph.scala.lib.util.Disposable
import com.glyph.scala.game.component.value.Transform
import com.glyph.scala.game.component.dungeon_actor.DungeonActor
import com.glyph.scala.game.component.dungeon_actor.DungeonActor.Direction

/**
 * @author glyph
 */
class ActorController(context:GameContext) extends Disposable{
  var actor :DungeonActor = null
  import Direction._
  context.eventManager += onInput
  def onInput(e:UIInputEvent):Boolean={
    e.typ match {
      case UIInputEvent.RIGHT_BUTTON => actor.tryMove(RIGHT)
      case UIInputEvent.LEFT_BUTTON => actor.tryMove(LEFT)
      case UIInputEvent.EXEC_BUTTON =>
      case _ =>
    }
    false
  }

  def dispose() {
    context.eventManager -= onInput
  }

  def setFocus(f:Entity){
    actor = f.get[DungeonActor]
  }
}
