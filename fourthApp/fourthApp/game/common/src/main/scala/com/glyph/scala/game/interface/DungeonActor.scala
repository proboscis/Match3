package com.glyph.scala.game.interface

import com.glyph.scala.lib.engine.Interface
import com.glyph.scala.game.dungeon.{TurnManager, TurnProcessor}
import com.glyph.scala.game.component.DTransform

/**
 * @author glyph
 */
class DungeonActor extends Interface with TurnProcessor{
  lazy val transform = owner.getMember[DTransform]
  def onMovePhase(manager: TurnManager) {}

  def onActionPhase(manager: TurnManager) {}

  def getPosition(): Int = transform.position
}
