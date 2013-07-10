package com.glyph.scala.lib.libgdx.screen

import com.badlogic.gdx.scenes.scene2d.ui.Table

/**
 * @author glyph
 */
trait TabledScreen extends StagedScreen{
  def DEBUG:Boolean
  val root = new Table
  root.setSize(STAGE_WIDTH,STAGE_HEIGHT)

  if(DEBUG)root.debug()
  stage.addActor(root)

  override def render(delta: Float) {
    super.render(delta)
    if (DEBUG)Table.drawDebug(stage)
  }
}
