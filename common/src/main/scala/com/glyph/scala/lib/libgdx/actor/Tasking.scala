package com.glyph.scala.lib.libgdx.actor

import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph.scala.lib.util.updatable.task.ParallelProcessor


/**
 * @author glyph
 */
trait Tasking extends Actor with ParallelProcessor{
  override def act(delta: Float) {
    super.act(delta)
    update(delta)
  }
}
