package com.glyph._scala.lib.libgdx.actor

import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph._scala.lib.util.pool.Poolable

/**
 * @author glyph
 */
trait PooledActor extends Actor with Poolable{
  override def freeToPool(){
    super.freeToPool()
    clear()
  }
}
