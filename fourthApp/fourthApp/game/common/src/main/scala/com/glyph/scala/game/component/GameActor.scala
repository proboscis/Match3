package com.glyph.scala.game.component

import com.glyph.scala.lib.entity_component_system.{Entity, Component}
import com.badlogic.gdx.scenes.scene2d.{Group, Actor}
import com.badlogic.gdx.graphics.g2d.SpriteBatch

/**
 * Created with IntelliJ IDEA.
 * User: glyph
 * Date: 13/04/11
 * Time: 11:10
 * To change this template use File | Settings | File Templates.
 */
class GameActor extends Actor with Component {
  private var renderer:GameActorRenderer = null
  setWidth(100)
  setHeight(100)
  override def initialize(owner: Entity) {
    super.initialize(owner)
    renderer = owner.get[GameActorRenderer]
  }

  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    super.draw(batch, parentAlpha)
    if (renderer != null){
      renderer.draw(batch,parentAlpha)
    }
  }
}
