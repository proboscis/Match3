package com.glyph.scala.game.component.dungeon_actor

import com.glyph.scala.game.dungeon.TurnManager
import com.glyph.scala.lib.engine.Entity
import com.glyph.scala.lib.engine.Entity.OnInitialize

/**
 * @author glyph
 */
abstract class AI(owner :Entity) {
  lazy val actor:DungeonActor = owner.get[DungeonActor]
  owner +=((e:OnInitialize)=>{actor})
  def onMovePhase(manager:TurnManager)
  def onActionPhase(manager:TurnManager)
}
