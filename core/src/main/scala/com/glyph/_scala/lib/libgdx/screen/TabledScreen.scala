package com.glyph._scala.lib.libgdx.screen

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.glyph._scala.lib.util.reactive.{Reactor, Var}
import scalaz._
import Scalaz._
/**
 * @author glyph
 */
trait TabledScreen extends StagedScreenBase with Reactor{
  def drawDebugTable:Boolean
  val debug = Var(false)
  val root = new Table
  root.setSize(STAGE_WIDTH,STAGE_HEIGHT)
  log("root size:",STAGE_WIDTH,STAGE_HEIGHT)
  if(drawDebugTable)root.debug()
  stage.addActor(new Table()<|(t=>{
    t.setFillParent(true)
    t.add(root).size(STAGE_WIDTH,STAGE_HEIGHT)
  }))
  override def render(delta: Float) {
    super.render(delta)
    if (drawDebugTable)Table.drawDebug(stage)
  }

  override def resize(w: Int, h: Int): Unit = {
    super.resize(w, h)
  }
}
