package com.glyph.scala.surface

import com.glyph.libgdx.surface.drawable.SurfaceDrawable
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.glyph.scala.lib.entity_component_system.EntityContainer
import com.glyph.scala.game.adapter.RendererAdapter

/**
 * SurfaceDrawable which draws all the renderComponent in the game context
 */
class GameRenderer(container:EntityContainer) extends SurfaceDrawable{
  def draw(batch: SpriteBatch, parentAlpha: Float) {
    container.getAdapters[RendererAdapter].foreach(_.rendererComponent.render(batch,parentAlpha))
  }
}
