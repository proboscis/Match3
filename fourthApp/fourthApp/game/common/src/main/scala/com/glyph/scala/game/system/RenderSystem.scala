package com.glyph.scala.game.system

import com.glyph.scala.game.event.{EntityRemoved, EntityAdded}
import com.glyph.scala.lib.util.Disposable
import com.glyph.libgdx.surface.drawable.SurfaceDrawable
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import java.util
import com.glyph.scala.lib.engine.EntityPackage
import com.glyph.scala.game.interface.renderer.Renderer
import com.glyph.scala.Glyph
import com.glyph.scala.game._

/**
 * @author glyph
 */
class RenderSystem(context: GameContext, pkg: EntityPackage) extends SurfaceDrawable with Disposable {
  final val TAG = "RenderSystem"
  context.eventManager += onAddEntity
  context.eventManager += onRemoveEntity
  lazy val entities = new util.LinkedList[Renderer]
  val iRenderer = pkg.getInterfaceIndex[Renderer]

  def zOrder(): Float = 0

  Glyph.log("RenderSystem", "construct")

  def draw(batch: SpriteBatch, parentAlpha: Float) {
    val it = entities.iterator()
    while (it.hasNext) {
      it.next().draw(batch, parentAlpha)
    }
  }

  def onAddEntity(e: EntityAdded): Boolean = {
    if (e.entity.hasInterface(iRenderer)) {
      entities.add(e.entity.getInterfaceI(iRenderer))
    }
    false
  }

  def onRemoveEntity(e: EntityRemoved): Boolean = {
    if (e.entity.hasInterface(iRenderer)) {
      entities.remove(e.entity.getInterfaceI(iRenderer))
    }
    false
  }

  def dispose() {
    context.eventManager -= onAddEntity
    context.eventManager -= onRemoveEntity
  }
}
