package com.glyph._scala.lib.libgdx.actor

import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph._scala.lib.util.updatable.task.ParallelProcessor


/**
 * @author glyph
 */
trait Tasking extends Actor with ParallelProcessor{
  override def act(delta: Float) {
    super.act(delta)
    update(delta)
  }
}
