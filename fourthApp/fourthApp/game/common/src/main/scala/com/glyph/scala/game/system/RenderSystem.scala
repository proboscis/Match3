package com.glyph.scala.game.system

import com.glyph.scala.game.event.{EntityRemoved, EntityAdded}
import com.glyph.scala.lib.util.Disposable
import com.glyph.scala.lib.engine.{GameContext, EntityPackage}
import com.glyph.scala.Glyph
import com.glyph.scala.game._
import com.badlogic.gdx.scenes.scene2d.{Actor, Group}
import component.renderer.Renderer
import com.glyph.scala.lib.util.actor.Scissor

/**
 * @author glyph
 */
class RenderSystem(context: GameContext, pkg: EntityPackage) extends Group with Disposable with Scissor{
  final val TAG = "RenderSystem"
  val root = new Group
  addActor(root)
  context  += onAddEntity
  context += onRemoveEntity
  val iRenderer = pkg.getIndex[Renderer]

  Glyph.deprecatedLog("RenderSystem", "construct")

  def onAddEntity(e: EntityAdded){
    if (e.entity.hasI(iRenderer)) {
      val renderer: Renderer = e.entity.getI(iRenderer)
      root.addActor(renderer)
    }
  }

  def onRemoveEntity(e: EntityRemoved){
    if (e.entity.hasI(iRenderer)) {
      val renderer: Renderer = e.entity.getI(iRenderer)
      root.removeActor(renderer)
    }
  }

  def addRenderer(renderer:Actor){
    root.addActor(renderer)
  }

  def dispose() {
    context-= onAddEntity
    context-= onRemoveEntity
  }

  override def setSize(width: Float, height: Float) {
    super.setSize(width, height)
    root.setSize(width,height)
  }
}
