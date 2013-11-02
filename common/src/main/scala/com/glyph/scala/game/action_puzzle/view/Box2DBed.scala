package com.glyph.scala.game.action_puzzle.view

import com.badlogic.gdx.physics.box2d.{Box2DDebugRenderer, World}
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.graphics.g2d.SpriteBatch

/**
 * @author glyph
 */
class Box2DBed(world:World) extends WidgetGroup{
  val renderer = new Box2DDebugRenderer(true,true,true,true,true)
  override def draw(batch: SpriteBatch, parentAlpha: Float){
    batch.flush()
    val proj = batch.getProjectionMatrix
    val trans = batch.getTransformMatrix
    renderer.render(world,getStage.getCamera.combined)
    batch.setProjectionMatrix(proj)
    batch.setTransformMatrix(trans)
  }
}
