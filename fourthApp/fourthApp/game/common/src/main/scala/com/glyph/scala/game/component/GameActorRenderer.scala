package com.glyph.scala.game.component

import com.glyph.scala.lib.entity_component_system.{Entity, Component}
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.glyph.scala.game.renderer.SimpleRenderer

/**
 * @author glyph
 */
class GameActorRenderer extends Component{
  private var actor :GameActor = null;
  private val renderer = new SimpleRenderer
  override def initialize(owner: Entity) {
    super.initialize(owner)
    actor = owner.get[GameActor]
  }

  def draw(batch:SpriteBatch,alpha:Float){
    if (actor != null){
      renderer.draw(actor,batch,alpha)
    }
  }
}
