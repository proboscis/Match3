package com.glyph.scala.lib.libgdx.actor

import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph.scala.lib.util.pool.Poolable

/**
 * @author proboscis
 */
trait FreeOnRemove extends Actor with Poolable{
  //remove is not only called when it's actually removed, so this doesn't work...
  override def remove(): Boolean = {
    freeToPool()
    super.remove()
  }
}
