package com.glyph.scala.lib.libgdx.actor

import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph.scala.lib.util.pool.Poolable

/**
 * @author glyph
 */
trait PooledActor extends Actor with Poolable{
  override def freeToPool(){
    super.freeToPool()
    clear()
  }
}
