package com.glyph.scala.game.system

import com.glyph.scala.game.event.{EntityRemoved, EntityAdded}
import com.glyph.scala.lib.util.{Scissor, Disposable}
import java.util
import com.glyph.scala.lib.engine.EntityPackage
import com.glyph.scala.Glyph
import com.glyph.scala.game._
import com.badlogic.gdx.scenes.scene2d.{Actor, Group}
import component.Renderer

/**
 * @author glyph
 */
class RenderSystem(context: GameContext, pkg: EntityPackage) extends Group with Disposable with Scissor{
  final val TAG = "RenderSystem"
  val root = new Group
  addActor(root)
  context.eventManager += onAddEntity
  context.eventManager += onRemoveEntity
  lazy val entities = new util.LinkedList[Renderer]
  val iRenderer = pkg.getIndex[Renderer]

  Glyph.log("RenderSystem", "construct")

  def onAddEntity(e: EntityAdded): Boolean = {
    if (e.entity.hasI(iRenderer)) {
      val renderer: Renderer = e.entity.getI(iRenderer)
      entities.add(renderer)
      root.addActor(renderer.delegate)
    }
    false
  }

  def onRemoveEntity(e: EntityRemoved): Boolean = {
    if (e.entity.hasI(iRenderer)) {
      val renderer: Renderer = e.entity.getI(iRenderer)
      entities.remove(renderer)
      root.removeActor(renderer.delegate)
    }
    false
  }

  def addRenderer(renderer:Actor){
    root.addActor(renderer)
  }

  def dispose() {
    context.eventManager -= onAddEntity
    context.eventManager -= onRemoveEntity
  }

  override def setSize(width: Float, height: Float) {
    super.setSize(width, height)
    root.setSize(width,height)
  }
}
