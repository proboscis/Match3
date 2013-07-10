package com.glyph.scala.lib.libgdx.actor

import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph.scala.lib.util.updatable.Updatables

/**
 * @author glyph
 */
trait Updating extends Actor with Updatables{
  override def act(delta: Float) {
    super.act(delta)
    update(delta)
  }
}
