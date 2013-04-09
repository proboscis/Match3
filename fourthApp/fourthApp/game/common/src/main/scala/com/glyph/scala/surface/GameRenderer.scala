package com.glyph.scala.surface

import com.glyph.libgdx.surface.drawable.SurfaceDrawable
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.glyph.scala.lib.entity_component_system.EntityContainer
import com.glyph.scala.game.adapter.RendererAdapter

/**
 * Created with IntelliJ IDEA.
 * User: glyph
 * Date: 13/04/08
 * Time: 20:07
 * To change this template use File | Settings | File Templates.
 */
class GameRenderer(container:EntityContainer) extends SurfaceDrawable{
  def draw(batch: SpriteBatch, parentAlpha: Float) {
    container.getAdapters[RendererAdapter].foreach(_.rendererComponent.render(batch,parentAlpha))
  }
}
