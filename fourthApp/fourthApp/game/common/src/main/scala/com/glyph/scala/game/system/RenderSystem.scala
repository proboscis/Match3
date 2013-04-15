package com.glyph.scala.game.system

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.glyph.libgdx.surface.drawable.SurfaceDrawable
import com.glyph.scala.lib.entity_component_system.EntityManager
import com.glyph.scala.game.adapter.RendererAdapter

/**
 * @author glyph
 */
class RenderSystem(container:EntityManager) extends SurfaceDrawable{
  override def draw(batch:SpriteBatch , alpha: Float){
    container.getAdapters[RendererAdapter].foreach(_.renderer.draw(batch,alpha))
  }
  def zOrder(): Float = 0
}
