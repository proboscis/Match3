package com.glyph.scala.lib.libgdx.screen

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.glyph.scala.lib.util.reactive.{Reactor, Var}
import scalaz._
import Scalaz._
/**
 * @author glyph
 */
trait TabledScreen extends StagedScreen with Reactor{
  val debug = Var(false)
  reactVar(config.debug.as[Boolean])(_ | false |> debug.update)
  val root = new Table
  root.setSize(STAGE_WIDTH,STAGE_HEIGHT)
  def DEBUG: Boolean =debug()
  if(DEBUG)root.debug()
  stage.addActor(root)
  override def render(delta: Float) {
    super.render(delta)
    if (DEBUG)Table.drawDebug(stage)
  }
}
