package com.glyph.scala.game.system

import com.glyph.scala.lib.entity_component_system.{GameContext, Entity, GameSystem}
import com.badlogic.gdx.graphics.Camera

/**
 * @author glyph
 */
class PlayerCameraSystem(game:GameContext,camera:Camera) extends GameSystem(game){
  var player: Entity = null

  override def update(delta: Float) {
    super.update(delta)
    if (player == null){
//      game.systemManager.getSystem[TagSystem].findEntity("player") match{
//        case Some(x) => player = x
//        case None =>
//      }
    }
  }

  override def dispose() {
    super.dispose()
    player = null
  }
}
